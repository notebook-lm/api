package vn.edu.fsoftacademy.api.application.query.currentuser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class GetCurrentUserQueryHandlerTest {
  private UserRepository userRepository;
  private GetCurrentUserQueryHandler handler;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    handler = new GetCurrentUserQueryHandler(userRepository);
  }

  @Test
  void handleReturnsProfileOfAuthenticatedUser() {
    var user = new User("user@example.com", "Original Name", "hashed-password");
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    var profile = handler.handle(new GetCurrentUserQuery(user.getId()));

    assertEquals(user.getId(), profile.id());
    assertEquals("user@example.com", profile.email());
    assertEquals("Original Name", profile.displayName());
  }

  @Test
  void handleThrowsUserNotFoundWhenAccountDoesNotExist() {
    UUID unknownId = UUID.randomUUID();
    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> handler.handle(new GetCurrentUserQuery(unknownId)));
  }
}
