package vn.edu.fsoftacademy.api.api.rest.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authenticated user and token pair returned by login or refresh.")
public record AuthResponse(
    AuthenticatedUserResponse user,
    @Schema(description = "JWT used in the Authorization Bearer header", example = "eyJhbGciOiJIUzI1NiJ9.access-token") String accessToken,
    @Schema(description = "Opaque/JWT token used only with refresh and logout endpoints", example = "eyJhbGciOiJIUzI1NiJ9.refresh-token") String refreshToken,
    @Schema(example = "Bearer") String tokenType,
    @Schema(description = "Access-token lifetime in seconds", example = "900") long expiresIn) {}
