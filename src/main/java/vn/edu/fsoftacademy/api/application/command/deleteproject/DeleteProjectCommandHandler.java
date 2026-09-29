package vn.edu.fsoftacademy.api.application.command.deleteproject;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class DeleteProjectCommandHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;
  private final ObjectStorage storage;

  public DeleteProjectCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents, ObjectStorage storage) {
    this.projects = projects;
    this.documents = documents;
    this.storage = storage;
  }

  public void execute(UUID ownerId, UUID projectId) {
    var project =
        projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    documents
        .findAllByProjectId(projectId)
        .forEach(document -> {
          storage.delete(document.getObjectKey());
          if (document.getExtractedContentObjectKey() != null) {
            storage.delete(document.getExtractedContentObjectKey());
          }
        });
    projects.delete(project);
  }
}
