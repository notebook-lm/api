package vn.edu.fsoftacademy.api.application.command.changepassword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class ChangePasswordCommandHandlerTest {
  private static final String PASSWORD_HASH = "hashed-password";
  private User user;
  private UserRepository users;
  private PasswordHasher passwords;
  private SessionTokenPort sessions;
  private ChangePasswordCommandHandler handler;

  @BeforeEach
  void setUp() {
    user = new User("user@example.com", "Original Name", PASSWORD_HASH);
    users = mock(UserRepository.class);
    passwords = mock(PasswordHasher.class);
    sessions = mock(SessionTokenPort.class);
    when(users.findById(user.getId())).thenReturn(Optional.of(user));
    when(passwords.matches("correct-password", PASSWORD_HASH)).thenReturn(true);
    when(passwords.matches(argThat(value -> !"correct-password".equals(value)), eq(PASSWORD_HASH)))
        .thenReturn(false);
    handler = new ChangePasswordCommandHandler(users, passwords, sessions);
  }

  @Test
  void hashesPersistsNewPasswordAndRevokesAllSessions() {
    when(passwords.hash("new-password")).thenReturn("new-hash");

    handler.execute(user.getId(), new ChangePasswordCommand("correct-password", "new-password"));

    assertEquals("new-hash", user.getPasswordHash());
    verify(users).save(user);
    verify(sessions).revokeAll(user.getId());
  }

  @Test
  void throwsInvalidCredentialsWhenCurrentPasswordIsWrong() {
    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.execute(user.getId(), new ChangePasswordCommand("wrong", "new-password")));

    verify(users, never()).save(any());
    verifyNoInteractions(sessions);
  }
}
