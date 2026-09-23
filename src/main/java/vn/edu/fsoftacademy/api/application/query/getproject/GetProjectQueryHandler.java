package vn.edu.fsoftacademy.api.application.query.getproject;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class GetProjectQueryHandler {
  private final ProjectRepository projects;
  public GetProjectQueryHandler(ProjectRepository projects) { this.projects = projects; }
  public GetProjectResult handle(UUID ownerId, UUID projectId) {
    var project = projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    return new GetProjectResult(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt());
  }
}
