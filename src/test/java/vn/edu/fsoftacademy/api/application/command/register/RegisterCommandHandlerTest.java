package vn.edu.fsoftacademy.api.application.command.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.RoleRepository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.Role;

class RegisterCommandHandlerTest {
  private static final String RAW_PASSWORD = "secret123";
  private UserRepository users;
  private RoleRepository roles;
  private PasswordHasher passwords;
  private SessionTokenPort sessions;
  private RegisterCommandHandler handler;

  @BeforeEach
  void setUp() {
    users = mock(UserRepository.class);
    roles = mock(RoleRepository.class);
    passwords = mock(PasswordHasher.class);
    sessions = mock(SessionTokenPort.class);
    when(roles.findByName("USER")).thenReturn(Optional.of(new Role("USER")));
    when(passwords.hash(RAW_PASSWORD)).thenReturn("hashed-secret");
    when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    handler = new RegisterCommandHandler(users, roles, passwords);
  }

  @Test
  void createsUserWithNormalizedEmailAndHashedPassword() {
    when(users.existsByEmail("alice@example.com")).thenReturn(false);

    var result =
        handler.execute(new RegisterCommand("  Alice@EXAMPLE.COM  ", "  Alice  ", RAW_PASSWORD));

    assertEquals("alice@example.com", result.email());
    assertEquals("Alice", result.displayName());
    verify(users).save(argThat(user -> user.getPasswordHash().equals("hashed-secret")));
  }

  @Test
  void throwsConflictWhenEmailAlreadyTaken() {
    when(users.existsByEmail("alice@example.com")).thenReturn(true);

    assertThrows(
        ConflictException.class,
        () -> handler.execute(new RegisterCommand("alice@example.com", "Alice", RAW_PASSWORD)));

    verify(users, never()).save(any());
  }

  @Test
  void returnsCreatedAccountWithoutIssuingTokens() {
    when(users.existsByEmail("bob@example.com")).thenReturn(false);

    var result = handler.execute(new RegisterCommand("bob@example.com", "Bob", RAW_PASSWORD));

    assertEquals("bob@example.com", result.email());
    verifyNoInteractions(sessions);
  }
}
