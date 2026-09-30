package vn.edu.fsoftacademy.api.api.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Current password and new password for the authenticated account.")
public record ChangePasswordRequest(
    @Schema(example = "secret123", format = "password") @NotBlank(message = "Current password is required.") String currentPassword,
    @Schema(example = "newSecret123", format = "password", minLength = 8, maxLength = 72)
        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters.")
        String newPassword) {}
