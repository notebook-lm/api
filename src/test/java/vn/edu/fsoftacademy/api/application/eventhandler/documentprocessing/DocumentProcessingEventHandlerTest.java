package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
            UUID.randomUUID(), "document.processing", projectId, documentId, "PROCESSING"));

    verify(commandHandler)
        .execute(
            new UpdateDocumentProcessingStatusCommand(
                projectId, documentId, DocumentProcessingStatus.PROCESSING));
  }

  @Test
  void rejectsUnexpectedEventType() {
    var eventHandler = new DocumentProcessingEventHandler(mock(UpdateDocumentProcessingStatusCommandHandler.class));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            eventHandler.handle(
                new DocumentProcessingEvent(
                    UUID.randomUUID(), "document.processed", UUID.randomUUID(), UUID.randomUUID(), "PROCESSING")));
  }
}
