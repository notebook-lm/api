package vn.edu.fsoftacademy.api.application.query.listdocuments;

import java.util.List;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class ListDocumentsQueryHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;

  public ListDocumentsQueryHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    this.projects = projects;
    this.documents = documents;
  }

  public List<ProjectDocument> handle(UUID ownerId, UUID projectId) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    return documents.findAllByProjectId(projectId);
  }
}
