package vn.edu.fsoftacademy.api.api.rest.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDocumentRequest(@NotBlank @Size(max = 255) String title) {}
