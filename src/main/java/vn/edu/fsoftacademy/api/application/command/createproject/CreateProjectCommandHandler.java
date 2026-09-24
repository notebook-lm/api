package vn.edu.fsoftacademy.api.application.command.createproject;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

public class CreateProjectCommandHandler {
  private final ProjectRepository projects;
  public CreateProjectCommandHandler(ProjectRepository projects) { this.projects = projects; }
  public CreateProjectResult execute(UUID ownerId, CreateProjectCommand command) {
    Project project = projects.save(new Project(ownerId, command.title().strip(), command.description()));
    return new CreateProjectResult(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt());
  }
}
