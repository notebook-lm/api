package vn.edu.fsoftacademy.api.api.rest.project.dto.response;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.Project;

public record ProjectResponse(
    UUID id, String title, String description, Instant createdAt, Instant updatedAt) {
  public static ProjectResponse from(Project project) {
    return new ProjectResponse(
        project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt());
  }
}
