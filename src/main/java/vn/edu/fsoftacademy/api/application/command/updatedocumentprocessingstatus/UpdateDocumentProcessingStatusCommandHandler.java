package vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus;

import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class UpdateDocumentProcessingStatusCommandHandler {
  private final ProjectDocumentRepository documents;

  public UpdateDocumentProcessingStatusCommandHandler(ProjectDocumentRepository documents) {
    this.documents = documents;
  }

  public ProjectDocument execute(UpdateDocumentProcessingStatusCommand command) {
    var document =
        documents
            .findByIdAndProjectId(command.documentId(), command.projectId())
            .orElseThrow(DocumentNotFoundException::new);
    document.updateProcessingStatus(command.status());
    return documents.save(document);
  }
}
