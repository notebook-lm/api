package vn.edu.fsoftacademy.api.api.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEvent;
import vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing.DocumentProcessingEventHandler;

class DocumentControllerTest {
  @Test
  void mapsValidKafkaDtoToApplicationEvent() {
    var eventHandler = mock(DocumentProcessingEventHandler.class);
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var eventId = UUID.randomUUID();
    var controller = new DocumentController(new ObjectMapper(), eventHandler);
    var payload = validPayload(eventId, documentId, projectId, "PROCESSING");

    controller.handleDocumentProcessing(payload);

    verify(eventHandler)
        .handle(
            new DocumentProcessingEvent(
                eventId, "document.processing", projectId, documentId, "PROCESSING"));
  }

  @Test
  void rejectsMalformedPayload() {
    var controller = new DocumentController(new ObjectMapper(), mock(DocumentProcessingEventHandler.class));

    var error =
        assertThrows(IllegalStateException.class, () -> controller.handleDocumentProcessing("not-json"));

    assertEquals("Could not process document.processing event", error.getMessage());
  }

  @Test
  void rejectsPayloadWithoutData() {
    var controller = new DocumentController(new ObjectMapper(), mock(DocumentProcessingEventHandler.class));

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
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var eventId = UUID.randomUUID();
    var event = new DocumentProcessingEvent(eventId, "document.processing", projectId, documentId, "PROCESSING");
    doThrow(new IllegalArgumentException("Unexpected event type: document.processed"))
        .when(eventHandler)
        .handle(event);
    var controller = new DocumentController(new ObjectMapper(), eventHandler);

    var error =
        assertThrows(
            IllegalStateException.class,
            () -> controller.handleDocumentProcessing(validPayload(eventId, documentId, projectId, "PROCESSING")));

    assertEquals("Could not process document.processing event", error.getMessage());
    assertEquals("Unexpected event type: document.processed", error.getCause().getMessage());
  }

  private String validPayload(UUID eventId, UUID documentId, UUID projectId, String status) {
    return """
        {"eventId":"%s","eventType":"document.processing","occurredAt":"2026-09-27T22:48:53.689623351Z","data":{"documentId":"%s","projectId":"%s","status":"%s"}}
        """
        .formatted(eventId, documentId, projectId, status);
  }
}
