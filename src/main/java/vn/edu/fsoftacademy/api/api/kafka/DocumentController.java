package vn.edu.fsoftacademy.api.api.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentFailureKafkaEvent;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentParsedKafkaEvent;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentProcessedKafkaEvent;
import vn.edu.fsoftacademy.api.api.kafka.dto.DocumentProcessingKafkaEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentparsedfailed.DocumentParsedFailedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentparsedfailed.DocumentParsedFailedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessed.DocumentProcessedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessedfailed.DocumentProcessedFailedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessedfailed.DocumentProcessedFailedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessed.DocumentProcessedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEventHandler;

@Component
public class DocumentController {
  private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
  private final ObjectMapper objectMapper;
  private final DocumentProcessingEventHandler documentProcessingEventHandler;
  private final DocumentProcessedEventHandler documentProcessedEventHandler;
  private final DocumentContentExtractedEventHandler documentContentExtractedEventHandler;
  private final DocumentParsedFailedEventHandler documentParsedFailedEventHandler;
  private final DocumentProcessedFailedEventHandler documentProcessedFailedEventHandler;

  public DocumentController(
      ObjectMapper objectMapper,
      DocumentProcessingEventHandler documentProcessingEventHandler,
      DocumentProcessedEventHandler documentProcessedEventHandler,
      DocumentContentExtractedEventHandler documentContentExtractedEventHandler,
      DocumentParsedFailedEventHandler documentParsedFailedEventHandler,
      DocumentProcessedFailedEventHandler documentProcessedFailedEventHandler) {
    this.objectMapper = objectMapper;
    this.documentProcessingEventHandler = documentProcessingEventHandler;
    this.documentProcessedEventHandler = documentProcessedEventHandler;
    this.documentContentExtractedEventHandler = documentContentExtractedEventHandler;
    this.documentParsedFailedEventHandler = documentParsedFailedEventHandler;
    this.documentProcessedFailedEventHandler = documentProcessedFailedEventHandler;
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-processing-topic}")
  public void handleDocumentProcessing(String payload) {
    try {
      var event = objectMapper.readValue(payload, DocumentProcessingKafkaEvent.class);
      if (event.eventId() == null || !"document.processing".equals(event.eventType()) || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null || event.data().projectId() == null || event.data().userId() == null) throw new IllegalArgumentException("Document processing event has required fields missing");
      var data = event.data();
      documentProcessingEventHandler.handle(new DocumentProcessingEvent(event.eventId(), event.eventType(), data.userId(), data.projectId(), data.documentId()));
      log.info("Applied document.processing event: eventId={}, documentId={}", event.eventId(), data.documentId());
    } catch (Exception exception) { throw new IllegalStateException("Could not process document.processing event", exception); }
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-processed-topic}")
  public void handleDocumentProcessed(String payload) {
    try {
      var event = objectMapper.readValue(payload, DocumentProcessedKafkaEvent.class);
      if (event.eventId() == null || !"document.processed".equals(event.eventType()) || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null || event.data().projectId() == null || event.data().userId() == null || event.data().chunkCount() < 0) throw new IllegalArgumentException("Document processed event has required fields missing");
      var data = event.data();
      documentProcessedEventHandler.handle(new DocumentProcessedEvent(event.eventId(), event.eventType(), data.userId(), data.projectId(), data.documentId(), data.chunkCount()));
      log.info("Applied document.processed event: eventId={}, documentId={}, chunkCount={}", event.eventId(), data.documentId(), data.chunkCount());
    } catch (Exception exception) { throw new IllegalStateException("Could not process document.processed event", exception); }
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-parsed-topic}")
  public void handleDocumentParsed(String payload) {
    try {
      var event = objectMapper.readValue(payload, DocumentParsedKafkaEvent.class);
      if (event.eventId() == null || !"document.parsed".equals(event.eventType()) || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null || event.data().projectId() == null || event.data().userId() == null || event.data().content() == null) throw new IllegalArgumentException("Document parsed event has required fields missing");
      var data = event.data();
      documentContentExtractedEventHandler.handle(new DocumentContentExtractedEvent(event.eventId(), event.eventType(), Instant.parse(event.occurredAt()), data.documentId(), data.projectId(), data.userId(), data.content()));
    } catch (Exception exception) { throw new IllegalStateException("Could not process document.parsed event", exception); }
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-parsed-failed-topic}")
  public void handleDocumentParsedFailed(String payload) {
    try {
      var event = objectMapper.readValue(payload, DocumentFailureKafkaEvent.class);
      if (event.eventId() == null || !"document.parsed.failed".equals(event.eventType()) || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null || event.data().projectId() == null || event.data().userId() == null || event.data().errorCode() == null || event.data().errorCode().isBlank() || event.data().errorMessage() == null || event.data().errorMessage().isBlank()) throw new IllegalArgumentException("Document parsed failure event has required fields missing");
      var data = event.data();
      documentParsedFailedEventHandler.handle(new DocumentParsedFailedEvent(event.eventId(), event.eventType(), data.userId(), data.projectId(), data.documentId(), data.errorCode(), data.errorMessage()));
      log.warn("Applied document.parsed.failed event: eventId={}, documentId={}, errorCode={}", event.eventId(), data.documentId(), data.errorCode());
    } catch (Exception exception) { throw new IllegalStateException("Could not process document.parsed.failed event", exception); }
  }

  @KafkaListener(topics = "${app.kafka.consumer.document-processed-failed-topic}")
  public void handleDocumentProcessedFailed(String payload) {
    try {
      var event = objectMapper.readValue(payload, DocumentFailureKafkaEvent.class);
      if (event.eventId() == null || !"document.processed.failed".equals(event.eventType()) || event.occurredAt() == null || event.occurredAt().isBlank() || event.data() == null || event.data().documentId() == null || event.data().projectId() == null || event.data().userId() == null || event.data().errorCode() == null || event.data().errorCode().isBlank() || event.data().errorMessage() == null || event.data().errorMessage().isBlank()) throw new IllegalArgumentException("Document processed failure event has required fields missing");
      var data = event.data();
      documentProcessedFailedEventHandler.handle(new DocumentProcessedFailedEvent(event.eventId(), event.eventType(), data.userId(), data.projectId(), data.documentId(), data.errorCode(), data.errorMessage()));
      log.warn("Applied document.processed.failed event: eventId={}, documentId={}, errorCode={}", event.eventId(), data.documentId(), data.errorCode());
    } catch (Exception exception) { throw new IllegalStateException("Could not process document.processed.failed event", exception); }
  }

}
