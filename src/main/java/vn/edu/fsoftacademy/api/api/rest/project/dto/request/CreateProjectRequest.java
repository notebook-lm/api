package vn.edu.fsoftacademy.api.api.rest.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    @Schema(example = "Research notes", minLength = 1, maxLength = 150) @NotBlank @Size(max = 150)
        String title,
    @Schema(example = "Sources and notes for the research topic", maxLength = 2000)
        @Size(max = 2000)
        String description) {}
