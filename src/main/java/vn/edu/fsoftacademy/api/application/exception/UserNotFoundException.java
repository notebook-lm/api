package vn.edu.fsoftacademy.api.application.exception;

/** Raised when an authenticated principal no longer has an account. */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {
    super("Authenticated user no longer exists");
  }
}
