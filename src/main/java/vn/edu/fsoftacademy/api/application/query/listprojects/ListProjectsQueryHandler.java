package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.util.List;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class ListProjectsQueryHandler {
  private final ProjectRepository projects;
  public ListProjectsQueryHandler(ProjectRepository projects) { this.projects = projects; }
  public List<ListProjectsResult> handle(UUID ownerId) {
    return projects.findAllByOwnerId(ownerId).stream()
        .map(project -> new ListProjectsResult(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt()))
        .toList();
  }
}
