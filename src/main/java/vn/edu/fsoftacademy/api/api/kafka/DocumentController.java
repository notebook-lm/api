package vn.edu.fsoftacademy.api.api.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentProcessingKafkaEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEventHandler;

@Component
public class DocumentController {
  private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

  private final ObjectMapper objectMapper;
  private final DocumentProcessingEventHandler eventHandler;

  public DocumentController(ObjectMapper objectMapper, DocumentProcessingEventHandler eventHandler) {
    this.objectMapper = objectMapper;
    this.eventHandler = eventHandler;
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
}
