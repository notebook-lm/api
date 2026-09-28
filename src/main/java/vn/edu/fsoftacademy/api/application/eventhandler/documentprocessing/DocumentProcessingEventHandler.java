package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus.UpdateDocumentProcessingStatusCommand;
import vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus.UpdateDocumentProcessingStatusCommandHandler;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public class DocumentProcessingEventHandler {
  private static final String EVENT_TYPE = "document.processing";

  private final UpdateDocumentProcessingStatusCommandHandler commandHandler;

  public DocumentProcessingEventHandler(
      UpdateDocumentProcessingStatusCommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  public void handle(DocumentProcessingEvent event) {
    if (!EVENT_TYPE.equals(event.eventType())) {
      throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
    }

    commandHandler.execute(
        new UpdateDocumentProcessingStatusCommand(
            event.projectId(),
            event.documentId(),
            DocumentProcessingStatus.valueOf(event.status())));
  }
}
