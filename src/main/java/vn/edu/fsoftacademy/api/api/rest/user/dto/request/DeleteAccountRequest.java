package vn.edu.fsoftacademy.api.api.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
    description = "Password confirmation required to permanently delete the authenticated account.")
public record DeleteAccountRequest(
    @Schema(example = "secret123", format = "password") @NotBlank String currentPassword) {}
