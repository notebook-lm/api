package vn.edu.fsoftacademy.api.api.rest.shared.error;

import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationExceptionHandler extends GlobalExceptionHandler {

  /** Authenticated principal's account no longer exists — treat as unauthorized. */
  @ExceptionHandler(UserNotFoundException.class)
  ResponseEntity<ApiError> userNotFound(UserNotFoundException ex) {
    return error(HttpStatus.UNAUTHORIZED, ApiErrorCode.AUTHENTICATION_REQUIRED, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  ResponseEntity<ApiError> invalidCredentials(InvalidCredentialsException ex) {
    return error(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_CREDENTIALS, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  ResponseEntity<ApiError> invalidRefreshToken(InvalidRefreshTokenException ex) {
    return error(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_REFRESH_TOKEN, ex.getMessage(), Map.of());
  }
}
