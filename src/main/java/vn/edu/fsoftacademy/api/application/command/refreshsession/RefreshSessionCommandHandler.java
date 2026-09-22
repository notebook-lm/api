package vn.edu.fsoftacademy.api.application.command.refreshsession;

import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;

/** Rotates a refresh token and creates a new authenticated session result. */
public class RefreshSessionCommandHandler {
  private final SessionTokenPort sessionTokenPort;

  public RefreshSessionCommandHandler(SessionTokenPort sessionTokenPort) {
    this.sessionTokenPort = sessionTokenPort;
  }

  public RefreshSessionResult execute(RefreshSessionCommand command) {
    var sessionTokens = sessionTokenPort.rotate(command.refreshToken());
    return new RefreshSessionResult(
        sessionTokens.user().getId(),
        sessionTokens.user().getEmail(),
        sessionTokens.user().getDisplayName(),
        sessionTokens.accessToken().value(),
        sessionTokens.refreshToken().value(),
        sessionTokens.accessToken().tokenType(),
        sessionTokens.accessToken().expiresIn());
  }
}
