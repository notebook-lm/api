package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class UploadDocumentCommandHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;
  private final ObjectStorage storage;

  public UploadDocumentCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents, ObjectStorage storage) {
    this.projects = projects;
    this.documents = documents;
    this.storage = storage;
  }

  public UploadDocumentResult execute(UUID ownerId, UUID projectId, UploadDocumentCommand command) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    var id = UUID.randomUUID();
    var key = "projects/" + projectId + "/documents/" + id;
    storage.put(key, command.content(), command.sizeBytes(), command.contentType());
    try {
      var document =
          documents.save(
              new ProjectDocument(
                  id,
                  projectId,
                  command.title(),
                  command.originalFilename(),
                  command.contentType(),
                  command.sizeBytes(),
                  key,
                  java.time.Instant.now(),
                  java.time.Instant.now()));
      return result(document);
    } catch (RuntimeException ex) {
      try {
        storage.delete(key);
      } catch (RuntimeException ignored) {
      }
      throw ex;
    }
  }

  private UploadDocumentResult result(ProjectDocument d) {
    return new UploadDocumentResult(
        d.getId(),
        d.getProjectId(),
        d.getTitle(),
        d.getOriginalFilename(),
        d.getContentType(),
        d.getSizeBytes(),
        d.getCreatedAt(),
        d.getUpdatedAt());
  }
}
