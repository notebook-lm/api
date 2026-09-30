package vn.edu.fsoftacademy.api.application.query.getdocument;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class GetDocumentQueryHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;

  public GetDocumentQueryHandler(ProjectRepository projects, ProjectDocumentRepository documents) {
    this.projects = projects;
    this.documents = documents;
  }

  public ProjectDocument handle(UUID ownerId, UUID projectId, UUID documentId) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    return documents
        .findByIdAndProjectId(documentId, projectId)
        .orElseThrow(DocumentNotFoundException::new);
  }
}
