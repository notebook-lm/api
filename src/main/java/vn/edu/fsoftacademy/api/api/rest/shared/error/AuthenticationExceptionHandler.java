package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;

@RestControllerAdvice
public class AuthenticationExceptionHandler extends GlobalExceptionHandler {

  /**
   * Authenticated principal's account no longer exists — treat as unauthorized.
   */
  @ExceptionHandler(UserNotFoundException.class)
  ResponseEntity<ApiError> userNotFound(UserNotFoundException ex) {
    return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
  }

  @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class})
  ResponseEntity<ApiError> unauthorized(RuntimeException ex) {
    return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
  }
}
