package vn.edu.fsoftacademy.api.api.rest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to create an account.")
public record RegisterRequest(
    @Schema(example = "user@example.com") @NotBlank @Email String email,
    @Schema(example = "Jane Doe", minLength = 2, maxLength = 100)
        @NotBlank
        @Size(min = 2, max = 100)
        String displayName,
    @Schema(example = "secret123", format = "password", minLength = 8, maxLength = 72)
        @NotBlank
        @Size(min = 8, max = 72)
        String password) {}
