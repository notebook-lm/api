package vn.edu.fsoftacademy.api.application.command.deletedocument;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class DeleteDocumentCommandHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;
  private final ObjectStorage storage;

  public DeleteDocumentCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents, ObjectStorage storage) {
    this.projects = projects;
    this.documents = documents;
    this.storage = storage;
  }

  public void execute(UUID ownerId, UUID projectId, UUID documentId) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    var document =
        documents
            .findByIdAndProjectId(documentId, projectId)
            .orElseThrow(DocumentNotFoundException::new);
    storage.delete(document.getObjectKey());
    documents.delete(document);
  }
}
