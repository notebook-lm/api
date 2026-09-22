package vn.edu.fsoftacademy.api.api.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "New email address confirmed with the current password.")
public record ChangeEmailRequest(
    @Schema(example = "new.email@example.com") @NotBlank @Email String email,
    @Schema(example = "secret123", format = "password") @NotBlank String currentPassword) {}
