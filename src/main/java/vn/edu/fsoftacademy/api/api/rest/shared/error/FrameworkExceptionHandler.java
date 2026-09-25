package vn.edu.fsoftacademy.api.api.rest.shared.error;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class FrameworkExceptionHandler extends GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(FrameworkExceptionHandler.class);

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiError> constraintViolation(ConstraintViolationException ex) {
    Map<String, String> fieldErrors =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage(),
                    (first, ignored) -> first));
    return error(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> unreadableMessage(HttpMessageNotReadableException ex) {
    return error(HttpStatus.BAD_REQUEST, "Request body is missing or malformed", Map.of());
  }

  @ExceptionHandler({MissingServletRequestParameterException.class, MissingRequestHeaderException.class})
  ResponseEntity<ApiError> missingRequestValue(Exception ex) {
    return error(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException ex) {
    String name = ex.getName();
    return error(HttpStatus.BAD_REQUEST, "Invalid value for '" + name + "'", Map.of(name, "Invalid value"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  ResponseEntity<ApiError> methodNotSupported(HttpRequestMethodNotSupportedException ex) {
    return error(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  ResponseEntity<ApiError> mediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
    return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  ResponseEntity<ApiError> resourceNotFound(NoResourceFoundException ex) {
    return error(HttpStatus.NOT_FOUND, "Endpoint not found", Map.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ApiError> accessDenied(AccessDeniedException ex) {
    return error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", Map.of());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception ex) {
    log.error("Unhandled API exception", ex);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", Map.of());
  }
}
