package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;

@RestControllerAdvice
public class BusinessExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiError> conflict(ConflictException ex) {
    return error(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
  }
}
