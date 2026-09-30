package vn.edu.fsoftacademy.api.application.command.refreshsession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.model.*;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.domain.entity.User;

class RefreshSessionCommandHandlerTest {
  @Test
  void delegatesRotationToSessionPort() {
    SessionTokenPort sessions = mock(SessionTokenPort.class);
    var user = new User("user@example.com", "User", "hash");
    when(sessions.rotate("old-token"))
        .thenReturn(
            new SessionTokens(
                user,
                new AccessToken("access-token", "Bearer", 3600L),
                new RefreshToken("refresh-token", 86400L)));

    var result =
        new RefreshSessionCommandHandler(sessions).execute(new RefreshSessionCommand("old-token"));

    verify(sessions).rotate("old-token");
    assertEquals("user@example.com", result.email());
    assertEquals("access-token", result.accessToken());
  }
}
