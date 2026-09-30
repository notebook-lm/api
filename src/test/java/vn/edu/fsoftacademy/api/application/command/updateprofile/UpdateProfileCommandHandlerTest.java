package vn.edu.fsoftacademy.api.application.command.updateprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class UpdateProfileCommandHandlerTest {
  private User user;
  private UserRepository users;
  private UpdateProfileCommandHandler handler;

  @BeforeEach
  void setUp() {
    user = new User("user@example.com", "Original Name", "hashed-password");
    users = mock(UserRepository.class);
    when(users.findById(user.getId())).thenReturn(Optional.of(user));
    when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    handler = new UpdateProfileCommandHandler(users);
  }

  @Test
  void persistsStrippedDisplayName() {
    var result = handler.execute(user.getId(), new UpdateProfileCommand("  New Name  "));

    assertEquals("New Name", result.displayName());
    verify(users).save(user);
  }

  @Test
  void preservesSingleWordName() {
    var result = handler.execute(user.getId(), new UpdateProfileCommand("Alice"));

    assertEquals("Alice", result.displayName());
  }
}
