package vn.edu.fsoftacademy.api.api.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentContentExtractedKafkaEvent;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentProcessingKafkaEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEventHandler;

@Component
public class DocumentController {
  private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

  private final ObjectMapper objectMapper;
  private final DocumentProcessingEventHandler eventHandler;
  private final DocumentContentExtractedEventHandler extractedContentEventHandler;

  public DocumentController(
      ObjectMapper objectMapper,
      DocumentProcessingEventHandler eventHandler,
      DocumentContentExtractedEventHandler extractedContentEventHandler) {
    this.objectMapper = objectMapper;
    this.eventHandler = eventHandler;
    this.extractedContentEventHandler = extractedContentEventHandler;
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-processing-topic}")
  public void handleDocumentProcessing(String payload) {
    try {
      var kafkaEvent = objectMapper.readValue(payload, DocumentProcessingKafkaEvent.class);
      if (kafkaEvent.data() == null) {
        throw new IllegalArgumentException("Event data is required");
      }

      var data = kafkaEvent.data();
      eventHandler.handle(
          new DocumentProcessingEvent(
              kafkaEvent.eventId(),
              kafkaEvent.eventType(),
              data.userId(),
              data.projectId(),
              data.documentId(),
              data.status()));
      log.info(
          "Applied document.processing event {}: document {} status {}",
          kafkaEvent.eventId(),
          data.documentId(),
          data.status());
    } catch (Exception ex) {
      throw new IllegalStateException("Could not process document.processing event", ex);
    }
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-content-extracted-topic}")
  public void handleDocumentContentExtracted(String payload) {
    try {
      var kafkaEvent = objectMapper.readValue(payload, DocumentContentExtractedKafkaEvent.class);
      validateExtractedContentEvent(kafkaEvent);
      if (!"document.content.extracted".equals(kafkaEvent.eventType())) {
        log.warn(
            "Ignored extracted content Kafka event: eventId={}, documentId={}, contentLength={}, result=unexpected_event_type",
            kafkaEvent.eventId(), kafkaEvent.data().documentId(), kafkaEvent.data().content().length());
        return;
      }
      var data = kafkaEvent.data();
      extractedContentEventHandler.handle(
          new DocumentContentExtractedEvent(
              kafkaEvent.eventId(),
              kafkaEvent.eventType(),
              Instant.parse(kafkaEvent.occurredAt()),
              data.documentId(),
              data.projectId(),
              data.userId(),
              data.content()));
    } catch (Exception ex) {
      throw new IllegalStateException("Could not process document.content.extracted event", ex);
    }
  }

  private void validateExtractedContentEvent(DocumentContentExtractedKafkaEvent event) {
    if (event.eventId() == null || event.eventType() == null || event.eventType().isBlank()
        || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null
        || event.data().projectId() == null || event.data().userId() == null
        || event.data().content() == null) {
      throw new IllegalArgumentException("Extracted content event has required fields missing");
    }
  }
}
