package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class ListProjectsQueryHandler {
  private final ProjectRepository projects;

  public ListProjectsQueryHandler(ProjectRepository projects) {
    this.projects = projects;
  }

  public ProjectPage handle(UUID ownerId, ListProjectsQuery query) {
    if (query.createdFrom() != null
        && query.createdTo() != null
        && query.createdFrom().isAfter(query.createdTo())) {
      throw new IllegalArgumentException("createdFrom must be before or equal to createdTo");
    }
    return projects.findPageByOwnerId(ownerId, query);
  }
}
