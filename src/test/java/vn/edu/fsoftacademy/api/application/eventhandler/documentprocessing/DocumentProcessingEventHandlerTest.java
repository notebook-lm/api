package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus.UpdateDocumentProcessingStatusCommand;
import vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus.UpdateDocumentProcessingStatusCommandHandler;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

class DocumentProcessingEventHandlerTest {
  @Test
  void mapsEventToStatusUpdateCommand() {
    var commandHandler = mock(UpdateDocumentProcessingStatusCommandHandler.class);
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var eventHandler = new DocumentProcessingEventHandler(commandHandler);

    eventHandler.handle(
        new DocumentProcessingEvent(
            UUID.randomUUID(),
            "document.processing",
            UUID.randomUUID(),
            projectId,
            documentId,
            "PROCESSING"));

    verify(commandHandler)
        .execute(
            new UpdateDocumentProcessingStatusCommand(
                projectId, documentId, DocumentProcessingStatus.PROCESSING));
  }

  @Test
  void supportsEachDocumentProcessingStatus() {
    var commandHandler = mock(UpdateDocumentProcessingStatusCommandHandler.class);
    var eventHandler = new DocumentProcessingEventHandler(commandHandler);

    for (var status : DocumentProcessingStatus.values()) {
      var projectId = UUID.randomUUID();
      var documentId = UUID.randomUUID();
      eventHandler.handle(
          new DocumentProcessingEvent(
              UUID.randomUUID(),
              "document.processing",
              UUID.randomUUID(),
              projectId,
              documentId,
              status.name()));
      verify(commandHandler)
          .execute(new UpdateDocumentProcessingStatusCommand(projectId, documentId, status));
    }
  }

  @Test
  void rejectsUnexpectedEventTypeBeforeCallingCommandHandler() {
    var commandHandler = mock(UpdateDocumentProcessingStatusCommandHandler.class);
    var eventHandler = new DocumentProcessingEventHandler(commandHandler);

    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                eventHandler.handle(
                    new DocumentProcessingEvent(
                        UUID.randomUUID(),
                        "document.processed",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "PROCESSING")));

    assertEquals("Unexpected event type: document.processed", error.getMessage());
    verifyNoInteractions(commandHandler);
  }

  @Test
  void rejectsUnknownStatusBeforeCallingCommandHandler() {
    var commandHandler = mock(UpdateDocumentProcessingStatusCommandHandler.class);
    var eventHandler = new DocumentProcessingEventHandler(commandHandler);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            eventHandler.handle(
                new DocumentProcessingEvent(
                    UUID.randomUUID(),
                    "document.processing",
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "FAILED")));

    verifyNoInteractions(commandHandler);
  }
}
