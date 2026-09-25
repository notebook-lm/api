package vn.edu.fsoftacademy.api.api.rest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token used to obtain a new token pair.")
public record RefreshTokenRequest(
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.refresh-token") @NotBlank(message = "Refresh token is required.") String refreshToken) {}
