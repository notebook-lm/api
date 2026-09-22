package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
        .collect(
            Collectors.toMap(
                e -> e.getField(),
                e -> e.getDefaultMessage() == null ? "Invalid value" : e.getDefaultMessage(),
                (a, b) -> a));
    return error(HttpStatus.BAD_REQUEST, "Validation failed", fields);
  }
}
