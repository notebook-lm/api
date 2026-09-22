package vn.edu.fsoftacademy.api.application.command.changeemail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class ChangeEmailCommandHandlerTest {
  private static final String PASSWORD_HASH = "hashed-password";
  private User user;
  private UserRepository users;
  private PasswordHasher passwords;
  private SessionTokenPort sessions;
  private ChangeEmailCommandHandler handler;

  @BeforeEach
  void setUp() {
    user = new User("user@example.com", "Original Name", PASSWORD_HASH);
    users = mock(UserRepository.class);
    passwords = mock(PasswordHasher.class);
    sessions = mock(SessionTokenPort.class);
    when(users.findById(user.getId())).thenReturn(Optional.of(user));
    when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(passwords.matches("correct-password", PASSWORD_HASH)).thenReturn(true);
    when(passwords.matches(argThat(value -> !"correct-password".equals(value)), eq(PASSWORD_HASH))).thenReturn(false);
    handler = new ChangeEmailCommandHandler(users, passwords, sessions);
  }

  @Test
  void updatesAndRevokesSessionsOnSuccess() {
    when(users.existsByEmail("newemail@example.com")).thenReturn(false);

    var result = handler.execute(user.getId(), new ChangeEmailCommand("  NewEmail@EXAMPLE.COM  ", "correct-password"));

    assertEquals("newemail@example.com", result.email());
    verify(users).save(user);
    verify(sessions).revokeAll(user.getId());
  }

  @Test
  void throwsInvalidCredentialsWhenPasswordIsWrong() {
    assertThrows(InvalidCredentialsException.class, () -> handler.execute(user.getId(), new ChangeEmailCommand("new@example.com", "wrong")));

    verifyNoInteractions(sessions);
  }

  @Test
  void throwsConflictWhenEmailTakenByAnotherAccount() {
    when(users.existsByEmail("taken@example.com")).thenReturn(true);

    assertThrows(ConflictException.class, () -> handler.execute(user.getId(), new ChangeEmailCommand("taken@example.com", "correct-password")));

    verify(users, never()).save(any());
  }

  @Test
  void allowsSameEmailWithoutConflict() {
    var result = handler.execute(user.getId(), new ChangeEmailCommand("user@example.com", "correct-password"));

    assertEquals("user@example.com", result.email());
    verify(sessions).revokeAll(user.getId());
  }
}
