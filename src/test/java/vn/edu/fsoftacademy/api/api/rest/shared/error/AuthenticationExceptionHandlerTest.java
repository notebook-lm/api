package vn.edu.fsoftacademy.api.api.rest.shared.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;

class AuthenticationExceptionHandlerTest {
  private final AuthenticationExceptionHandler handler = new AuthenticationExceptionHandler();

  @Test
  void mapsInvalidCredentialsToUnauthorized() {
    assertEquals(
        401, handler.invalidCredentials(new InvalidCredentialsException()).getStatusCode().value());
    assertEquals(
        ApiErrorCode.INVALID_CREDENTIALS,
        handler.invalidCredentials(new InvalidCredentialsException()).getBody().code());
  }

  @Test
  void mapsInvalidRefreshTokenToUnauthorized() {
    assertEquals(
        401, handler.invalidRefreshToken(new InvalidRefreshTokenException()).getStatusCode().value());
    assertEquals(
        ApiErrorCode.INVALID_REFRESH_TOKEN,
        handler.invalidRefreshToken(new InvalidRefreshTokenException()).getBody().code());
  }

  @Test
  void mapsMissingAuthenticatedUserToUnauthorized() {
    assertEquals(401, handler.userNotFound(new UserNotFoundException()).getStatusCode().value());
  }
}
