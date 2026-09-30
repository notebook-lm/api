package vn.edu.fsoftacademy.api.api.rest.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/** Response returned after an account is created; authentication requires a separate login. */
@Schema(description = "Newly created account; tokens are not issued during registration.")
public record RegistrationResponse(
    @Schema(format = "uuid", example = "4f04e1bc-f5d3-496d-9307-0bbe9dc04ee6") UUID id,
    @Schema(example = "user@example.com") String email,
    @Schema(example = "Jane Doe") String displayName) {}
