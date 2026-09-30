package vn.edu.fsoftacademy.api.api.rest.shared.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error response.")
public record ApiError(
    @Schema(format = "date-time") Instant timestamp,
    @Schema(description = "HTTP status code.", example = "400") int status,
    @Schema(description = "HTTP reason phrase.", example = "Bad Request") String error,
    @Schema(description = "Stable machine-readable error identifier for client logic.", example = "VALIDATION_FAILED") ApiErrorCode code,
    @Schema(description = "Human-readable error explanation. For validation, this is the first field error.", example = "must not be blank") String message,
    @Schema(
            description = "Present for validation failures only; maps request field names to validation messages",
            example = "{\"email\":\"must be a well-formed email address\"}")
        Map<String, String> fieldErrors) {}
