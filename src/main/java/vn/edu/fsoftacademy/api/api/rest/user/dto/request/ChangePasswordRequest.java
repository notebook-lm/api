package vn.edu.fsoftacademy.api.api.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Current password and new password for the authenticated account.")
public record ChangePasswordRequest(
    @Schema(example = "secret123", format = "password") @NotBlank String currentPassword,
    @Schema(example = "newSecret123", format = "password", minLength = 8, maxLength = 72)
        @NotBlank
        @Size(min = 8, max = 72)
        String newPassword) {}
