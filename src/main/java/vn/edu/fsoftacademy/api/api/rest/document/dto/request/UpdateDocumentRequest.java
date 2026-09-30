package vn.edu.fsoftacademy.api.api.rest.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDocumentRequest(@NotBlank(message = "Document title is required.")
    @Size(max = 255, message = "Document title must not exceed 255 characters.") String title) {}
