package vn.edu.fsoftacademy.api.api.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEventHandler;

class DocumentControllerTest {
  @Test
  void mapsValidKafkaDtoToApplicationEvent() {
    var eventHandler = mock(DocumentProcessingEventHandler.class);
    var userId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var eventId = UUID.randomUUID();
    var controller = new DocumentController(new ObjectMapper(), eventHandler, mock(vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler.class));
    var payload = validPayload(eventId, userId, documentId, projectId, "PROCESSING");

    controller.handleDocumentProcessing(payload);

    verify(eventHandler)
        .handle(
            new DocumentProcessingEvent(
                eventId, "document.processing", userId, projectId, documentId, "PROCESSING"));
  }

  @Test
  void rejectsMalformedPayload() {
    var controller = new DocumentController(new ObjectMapper(), mock(DocumentProcessingEventHandler.class), mock(vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler.class));

    var error =
        assertThrows(IllegalStateException.class, () -> controller.handleDocumentProcessing("not-json"));

    assertEquals("Could not process document.processing event", error.getMessage());
  }

  @Test
  void rejectsPayloadWithoutData() {
    var controller = new DocumentController(new ObjectMapper(), mock(DocumentProcessingEventHandler.class), mock(vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler.class));

    var error =
        assertThrows(
            IllegalStateException.class,
            () ->
                controller.handleDocumentProcessing(
                    "{\"eventId\":\"%s\",\"eventType\":\"document.processing\",\"occurredAt\":\"2026-09-27T22:48:53Z\",\"data\":null}"
                        .formatted(UUID.randomUUID())));

    assertEquals("Could not process document.processing event", error.getMessage());
    assertEquals("Event data is required", error.getCause().getMessage());
  }

  @Test
  void wrapsApplicationHandlerFailure() {
    var eventHandler = mock(DocumentProcessingEventHandler.class);
    var userId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var eventId = UUID.randomUUID();
    var event = new DocumentProcessingEvent(
        eventId, "document.processing", userId, projectId, documentId, "PROCESSING");
    doThrow(new IllegalArgumentException("Unexpected event type: document.processed"))
        .when(eventHandler)
        .handle(event);
    var controller = new DocumentController(new ObjectMapper(), eventHandler, mock(vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted.DocumentContentExtractedEventHandler.class));

    var error =
        assertThrows(
            IllegalStateException.class,
            () -> controller.handleDocumentProcessing(validPayload(eventId, userId, documentId, projectId, "PROCESSING")));

    assertEquals("Could not process document.processing event", error.getMessage());
    assertEquals("Unexpected event type: document.processed", error.getCause().getMessage());
  }


  @Test
  void mapsValidExtractedContentEvent() {
    var processingHandler = mock(DocumentProcessingEventHandler.class);
    var extractedHandler = mock(DocumentContentExtractedEventHandler.class);
    var eventId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var controller = new DocumentController(new ObjectMapper(), processingHandler, extractedHandler);

    controller.handleDocumentContentExtracted(
        "{\"eventId\":\"%s\",\"eventType\":\"document.content.extracted\",\"occurredAt\":\"2026-09-29T00:00:00Z\",\"data\":{\"documentId\":\"%s\",\"projectId\":\"%s\",\"userId\":\"%s\",\"content\":\"parsed text\"}}"
            .formatted(eventId, documentId, projectId, userId));

    verify(extractedHandler).handle(new DocumentContentExtractedEvent(
        eventId, "document.content.extracted", java.time.Instant.parse("2026-09-29T00:00:00Z"),
        documentId, projectId, userId, "parsed text"));
  }

  @Test
  void rejectsExtractedContentPayloadMissingRequiredFields() {
    var controller = new DocumentController(
        new ObjectMapper(), mock(DocumentProcessingEventHandler.class), mock(DocumentContentExtractedEventHandler.class));

    var error = assertThrows(IllegalStateException.class, () -> controller.handleDocumentContentExtracted(
        "{\"eventId\":\"%s\",\"eventType\":\"document.content.extracted\",\"occurredAt\":\"2026-09-29T00:00:00Z\",\"data\":{}}".formatted(UUID.randomUUID())));

    assertEquals("Could not process document.content.extracted event", error.getMessage());
  }

  private String validPayload(
      UUID eventId, UUID userId, UUID documentId, UUID projectId, String status) {
    return """
        {"eventId":"%s","eventType":"document.processing","occurredAt":"2026-09-27T22:48:53.689623351Z","data":{"userId":"%s","documentId":"%s","projectId":"%s","status":"%s"}}
        """
        .formatted(eventId, userId, documentId, projectId, status);
  }
}
