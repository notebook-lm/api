package vn.edu.fsoftacademy.api.api.rest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to create an account.")
public record RegisterRequest(
    @Schema(example = "user@example.com") @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.") String email,
    @Schema(example = "Jane Doe", minLength = 2, maxLength = 100)
        @NotBlank(message = "Display name is required.")
        @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters.")
        String displayName,
    @Schema(example = "secret123", format = "password", minLength = 8, maxLength = 72)
        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        String password) {}
