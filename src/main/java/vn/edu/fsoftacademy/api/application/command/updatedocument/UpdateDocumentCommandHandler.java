package vn.edu.fsoftacademy.api.application.command.updatedocument;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class UpdateDocumentCommandHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;

  public UpdateDocumentCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    this.projects = projects;
    this.documents = documents;
  }

  public ProjectDocument execute(
      UUID ownerId, UUID projectId, UUID documentId, UpdateDocumentCommand command) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    var document =
        documents
            .findByIdAndProjectId(documentId, projectId)
            .orElseThrow(DocumentNotFoundException::new);
    document.rename(command.title());
    return documents.save(document);
  }
}
