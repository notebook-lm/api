package vn.edu.fsoftacademy.api.api.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Mutable profile fields for the authenticated account.")
public record UpdateProfileRequest(@Schema(example = "Jane Doe", minLength = 2, maxLength = 100) @NotBlank @Size(min = 2, max = 100) String displayName) {}
