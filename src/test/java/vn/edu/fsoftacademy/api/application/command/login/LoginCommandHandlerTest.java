package vn.edu.fsoftacademy.api.application.command.login;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.model.*;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class LoginCommandHandlerTest {
  private static final String RAW_PASSWORD = "secret123";
  private static final String PASSWORD_HASH = "hashed-secret";
  private UserRepository users;
  private PasswordHasher passwords;
  private SessionTokenPort sessions;
  private LoginCommandHandler handler;

  @BeforeEach
  void setUp() {
    users = mock(UserRepository.class);
    passwords = mock(PasswordHasher.class);
    sessions = mock(SessionTokenPort.class);
    when(passwords.matches(RAW_PASSWORD, PASSWORD_HASH)).thenReturn(true);
    when(passwords.matches(argThat(value -> !RAW_PASSWORD.equals(value)), eq(PASSWORD_HASH)))
        .thenReturn(false);
    handler = new LoginCommandHandler(users, passwords, sessions);
  }

  @Test
  void issuesSessionForValidCredentials() {
    var user = new User("alice@example.com", "Alice", PASSWORD_HASH);
    when(users.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
    when(sessions.issue(user)).thenReturn(session(user));

    var result = handler.execute(new LoginCommand("  Alice@EXAMPLE.COM  ", RAW_PASSWORD));

    assertEquals("alice@example.com", result.email());
    assertEquals("access-token", result.accessToken());
    assertEquals("refresh-token", result.refreshToken());
    verify(sessions).issue(user);
  }

  @Test
  void throwsInvalidCredentialsWhenEmailUnknown() {
    when(users.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.execute(new LoginCommand("unknown@example.com", RAW_PASSWORD)));
  }

  @Test
  void throwsInvalidCredentialsWhenAccountIsDisabled() {
    var user =
        new User(
            UUID.randomUUID(),
            "alice@example.com",
            "Alice",
            PASSWORD_HASH,
            false,
            java.time.Instant.now(),
            java.time.Instant.now());
    when(users.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.execute(new LoginCommand("alice@example.com", RAW_PASSWORD)));
    verify(sessions, never()).issue(any());
  }

  @Test
  void throwsInvalidCredentialsWhenPasswordIsWrong() {
    var user = new User("alice@example.com", "Alice", PASSWORD_HASH);
    when(users.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.execute(new LoginCommand("alice@example.com", "wrong")));
    verifyNoInteractions(sessions);
  }

  private SessionTokens session(User user) {
    return new SessionTokens(
        user,
        new AccessToken("access-token", "Bearer", 3600L),
        new RefreshToken("refresh-token", 86400L));
  }
}
