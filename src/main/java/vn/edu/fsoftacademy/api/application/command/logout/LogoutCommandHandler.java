package vn.edu.fsoftacademy.api.application.command.logout;

import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;

/** Revokes the refresh token supplied by the client, ending the current session. */
public class LogoutCommandHandler {
  private final SessionTokenPort sessionTokenPort;

  public LogoutCommandHandler(SessionTokenPort sessionTokenPort) {
    this.sessionTokenPort = sessionTokenPort;
  }

  public void execute(LogoutCommand command) {
    String refreshToken = command.refreshToken();
    sessionTokenPort.revoke(refreshToken);
  }
}
