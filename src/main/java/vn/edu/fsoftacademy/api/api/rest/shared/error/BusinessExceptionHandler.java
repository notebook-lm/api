package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.StorageException;

@RestControllerAdvice
public class BusinessExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiError> conflict(ConflictException ex) {
    return error(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> invalidArgument(IllegalArgumentException ex) {
    return error(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(ProjectNotFoundException.class)
  ResponseEntity<ApiError> projectNotFound(ProjectNotFoundException ex) {
    return error(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(DocumentNotFoundException.class)
  ResponseEntity<ApiError> documentNotFound(DocumentNotFoundException ex) {
    return error(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(StorageException.class)
  ResponseEntity<ApiError> storage(StorageException ex) {
    return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), Map.of());
  }
}
