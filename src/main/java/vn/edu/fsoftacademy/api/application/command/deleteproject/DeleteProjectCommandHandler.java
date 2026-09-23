package vn.edu.fsoftacademy.api.application.command.deleteproject;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class DeleteProjectCommandHandler {
  private final ProjectRepository projects;
  public DeleteProjectCommandHandler(ProjectRepository projects) { this.projects = projects; }
  public void execute(UUID ownerId, UUID projectId) {
    var project = projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    projects.delete(project);
  }
}
