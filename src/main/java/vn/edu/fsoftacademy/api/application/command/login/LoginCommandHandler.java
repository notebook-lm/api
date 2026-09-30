package vn.edu.fsoftacademy.api.application.command.login;

import java.util.Locale;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

/** Authenticates a user with email and password, then creates an authenticated session. */
public class LoginCommandHandler {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final SessionTokenPort sessionTokenPort;

  public LoginCommandHandler(
      UserRepository userRepository,
      PasswordHasher passwordHasher,
      SessionTokenPort sessionTokenPort) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.sessionTokenPort = sessionTokenPort;
  }

  public LoginResult execute(LoginCommand command) {
    String normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);
    User user =
        userRepository.findByEmail(normalizedEmail).orElseThrow(InvalidCredentialsException::new);

    if (!user.isEnabled()) {
      throw new InvalidCredentialsException();
    }

    if (!passwordHasher.matches(command.password(), user.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    var sessionTokens = sessionTokenPort.issue(user);
    return new LoginResult(
        sessionTokens.user().getId(),
        sessionTokens.user().getEmail(),
        sessionTokens.user().getDisplayName(),
        sessionTokens.accessToken().value(),
        sessionTokens.refreshToken().value(),
        sessionTokens.accessToken().tokenType(),
        sessionTokens.accessToken().expiresIn());
  }
}
