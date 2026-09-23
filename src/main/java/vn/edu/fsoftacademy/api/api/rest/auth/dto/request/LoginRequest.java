package vn.edu.fsoftacademy.api.api.rest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials used to authenticate an account.")
public record LoginRequest(
    @Schema(description = "Account email address", example = "user@example.com") @NotBlank @Email String email,
    @Schema(description = "Account password", example = "secret123", format = "password") @NotBlank String password) {}
