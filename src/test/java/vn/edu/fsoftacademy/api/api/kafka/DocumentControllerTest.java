package vn.edu.fsoftacademy.api.api.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    var payload =
        """
        {"eventId":"%s","eventType":"document.processing","occurredAt":"2026-09-27T22:48:53.689623351Z","data":{"documentId":"%s","projectId":"%s","status":"PROCESSING"}}
        """
            .formatted(eventId, documentId, projectId);

    controller.handleDocumentProcessing(payload);

    verify(eventHandler)
        .handle(
            new DocumentProcessingEvent(
                eventId, "document.processing", projectId, documentId, "PROCESSING"));
  }

  @Test
  void rejectsMalformedPayload() {
    var eventHandler = mock(DocumentProcessingEventHandler.class);
    var controller = new DocumentController(new ObjectMapper(), eventHandler);

    assertThrows(IllegalStateException.class, () -> controller.handleDocumentProcessing("not-json"));
  }
}
