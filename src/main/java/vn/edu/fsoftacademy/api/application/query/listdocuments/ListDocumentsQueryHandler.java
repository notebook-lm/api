package vn.edu.fsoftacademy.api.application.query.listdocuments;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class ListDocumentsQueryHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;

  public ListDocumentsQueryHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    this.projects = projects;
    this.documents = documents;
  }

  public ListDocumentsResult handle(UUID ownerId, UUID projectId, ListDocumentsQuery query) {
    if (query.createdFrom() != null
        && query.createdTo() != null
        && query.createdFrom().isAfter(query.createdTo())) {
      throw new IllegalArgumentException("createdFrom must be before or equal to createdTo");
    }
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    var page = documents.findPageByProjectId(projectId, query);
    return new ListDocumentsResult(
        page.items(),
        page.page(),
        page.size(),
        page.totalItems(),
        page.totalPages(),
        page.hasNext(),
        page.hasPrevious());
  }
}
