package vn.edu.fsoftacademy.api.api.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
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

class DocumentControllerTest {
  @Test
  void routesDocumentProcessingWithoutStatus() {
    var processingHandler = mock(DocumentProcessingEventHandler.class);
    var userId = UUID.randomUUID(); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID(); var eventId = UUID.randomUUID();
    var controller = controller(processingHandler, mock(DocumentProcessedEventHandler.class), mock(DocumentContentExtractedEventHandler.class));

    controller.handleDocumentProcessing("""
        {"eventId":"%s","eventType":"document.processing","occurredAt":"2026-09-27T22:48:53Z","data":{"userId":"%s","documentId":"%s","projectId":"%s"}}
        """.formatted(eventId, userId, documentId, projectId));

    verify(processingHandler).handle(new DocumentProcessingEvent(eventId, "document.processing", userId, projectId, documentId));
  }

  @Test
  void routesDocumentProcessedSeparately() {
    var processedHandler = mock(DocumentProcessedEventHandler.class);
    var userId = UUID.randomUUID(); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID(); var eventId = UUID.randomUUID();
    var controller = controller(mock(DocumentProcessingEventHandler.class), processedHandler, mock(DocumentContentExtractedEventHandler.class));

    controller.handleDocumentProcessed("""
        {"eventId":"%s","eventType":"document.processed","occurredAt":"2026-09-27T22:48:53Z","data":{"userId":"%s","documentId":"%s","projectId":"%s","chunkCount":3}}
        """.formatted(eventId, userId, documentId, projectId));

    verify(processedHandler).handle(new DocumentProcessedEvent(eventId, "document.processed", userId, projectId, documentId, 3));
  }

  @Test
  void rejectsMalformedProcessedPayload() {
    var error = assertThrows(IllegalStateException.class, () -> controller(mock(), mock(), mock(), mock(), mock()).handleDocumentProcessed("not-json"));
    assertEquals("Could not process document.processed event", error.getMessage());
  }

  @Test
  void routesDocumentParsed() {
    var extractedHandler = mock(DocumentContentExtractedEventHandler.class);
    var eventId = UUID.randomUUID(); var userId = UUID.randomUUID(); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID();
    var controller = controller(mock(DocumentProcessingEventHandler.class), mock(DocumentProcessedEventHandler.class), extractedHandler);

    controller.handleDocumentParsed("""
        {"eventId":"%s","eventType":"document.parsed","occurredAt":"2026-09-29T00:00:00Z","data":{"documentId":"%s","projectId":"%s","userId":"%s","content":"parsed text"}}
        """.formatted(eventId, documentId, projectId, userId));

    verify(extractedHandler).handle(new DocumentContentExtractedEvent(eventId, "document.parsed", Instant.parse("2026-09-29T00:00:00Z"), documentId, projectId, userId, "parsed text"));
  }

  @Test
  void routesParsedAndProcessedFailuresSeparately() {
    var parsedFailedHandler = mock(DocumentParsedFailedEventHandler.class);
    var processedFailedHandler = mock(DocumentProcessedFailedEventHandler.class);
    var userId = UUID.randomUUID(); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID();
    var controller = controller(mock(DocumentProcessingEventHandler.class), mock(DocumentProcessedEventHandler.class), mock(DocumentContentExtractedEventHandler.class), parsedFailedHandler, processedFailedHandler);
    var parsedEventId = UUID.randomUUID(); var processedEventId = UUID.randomUUID();

    controller.handleDocumentParsedFailed(failurePayload(parsedEventId, "document.parsed.failed", userId, projectId, documentId));
    controller.handleDocumentProcessedFailed(failurePayload(processedEventId, "document.processed.failed", userId, projectId, documentId));

    verify(parsedFailedHandler).handle(new DocumentParsedFailedEvent(parsedEventId, "document.parsed.failed", userId, projectId, documentId, "ParseError", "cannot parse"));
    verify(processedFailedHandler).handle(new DocumentProcessedFailedEvent(processedEventId, "document.processed.failed", userId, projectId, documentId, "ParseError", "cannot parse"));
  }

  private String failurePayload(UUID eventId, String eventType, UUID userId, UUID projectId, UUID documentId) {
    return """
        {"eventId":"%s","eventType":"%s","occurredAt":"2026-09-27T22:48:53Z","data":{"userId":"%s","documentId":"%s","projectId":"%s","errorCode":"ParseError","errorMessage":"cannot parse"}}
        """.formatted(eventId, eventType, userId, documentId, projectId);
  }

  private DocumentController controller(DocumentProcessingEventHandler processingHandler, DocumentProcessedEventHandler processedHandler, DocumentContentExtractedEventHandler extractedHandler) {
    return controller(processingHandler, processedHandler, extractedHandler, mock(DocumentParsedFailedEventHandler.class), mock(DocumentProcessedFailedEventHandler.class));
  }

  private DocumentController controller(DocumentProcessingEventHandler processingHandler, DocumentProcessedEventHandler processedHandler, DocumentContentExtractedEventHandler extractedHandler, DocumentParsedFailedEventHandler parsedFailedHandler, DocumentProcessedFailedEventHandler processedFailedHandler) {
    return new DocumentController(new ObjectMapper(), processingHandler, processedHandler, extractedHandler, parsedFailedHandler, processedFailedHandler);
  }
}
