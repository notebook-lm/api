package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class GlobalExceptionHandler {

  protected ResponseEntity<ApiError> error(
      HttpStatus status, String message, Map<String, String> fields) {
    String resolvedMessage =
        message == null || message.isBlank() ? status.getReasonPhrase() : message;
    return ResponseEntity.status(status)
        .body(
            new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(), resolvedMessage, fields));
  }
}
