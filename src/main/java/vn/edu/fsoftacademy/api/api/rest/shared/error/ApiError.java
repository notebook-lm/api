package vn.edu.fsoftacademy.api.api.rest.shared.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error response.")
public record ApiError(
    @Schema(format = "date-time") Instant timestamp,
    @Schema(example = "400") int status,
    @Schema(example = "Bad Request") String error,
    @Schema(example = "Validation failed") String message,
    @Schema(description = "Validation messages keyed by request field", example = "{\"email\":\"must be a well-formed email address\"}") Map<String, String> fieldErrors) {}
