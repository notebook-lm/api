package vn.edu.fsoftacademy.api.application.command.deleteaccount;

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

class DeleteAccountCommandHandlerTest {
  private static final String PASSWORD_HASH = "hashed-password";
  private User user;
  private UserRepository users;
  private PasswordHasher passwords;
  private SessionTokenPort sessions;
  private DeleteAccountCommandHandler handler;

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
    handler = new DeleteAccountCommandHandler(users, passwords, sessions);
  }

  @Test
  void deletesSessionsThenUserOnSuccess() {
    handler.execute(user.getId(), new DeleteAccountCommand("correct-password"));

    var order = inOrder(sessions, users);
    order.verify(sessions).deleteAll(user.getId());
    order.verify(users).delete(user);
  }

  @Test
  void throwsInvalidCredentialsWhenPasswordIsWrong() {
    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.execute(user.getId(), new DeleteAccountCommand("wrong")));

    verifyNoInteractions(sessions);
    verify(users, never()).delete(any());
  }
}
