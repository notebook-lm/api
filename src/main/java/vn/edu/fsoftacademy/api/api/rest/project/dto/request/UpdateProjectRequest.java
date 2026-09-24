package vn.edu.fsoftacademy.api.api.rest.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
    @Schema(example = "Updated research notes", minLength = 1, maxLength = 150) @NotBlank @Size(max = 150) String title,
    @Schema(example = "Updated project description", maxLength = 2000) @Size(max = 2000) String description) {}
