package vn.edu.fsoftacademy.api.api.rest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token to revoke.")
public record LogoutRequest(
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.refresh-token") @NotBlank String refreshToken) {}
