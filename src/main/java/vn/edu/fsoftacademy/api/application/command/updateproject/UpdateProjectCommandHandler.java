package vn.edu.fsoftacademy.api.application.command.updateproject;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class UpdateProjectCommandHandler {
  private final ProjectRepository projects;
  public UpdateProjectCommandHandler(ProjectRepository projects) { this.projects = projects; }
  public UpdateProjectResult execute(UUID ownerId, UUID projectId, UpdateProjectCommand command) {
    var project = projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    project.update(command.title().strip(), command.description());
    projects.save(project);
    return new UpdateProjectResult(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt());
  }
}
