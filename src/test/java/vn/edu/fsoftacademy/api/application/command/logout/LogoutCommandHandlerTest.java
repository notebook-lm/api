package vn.edu.fsoftacademy.api.application.command.logout;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;

class LogoutCommandHandlerTest {
  @Test
  void revokesTheSuppliedRefreshToken() {
    SessionTokenPort sessions = mock(SessionTokenPort.class);

    new LogoutCommandHandler(sessions).execute(new LogoutCommand("my-refresh-token"));

    verify(sessions).revoke("my-refresh-token");
    verifyNoMoreInteractions(sessions);
  }
}
