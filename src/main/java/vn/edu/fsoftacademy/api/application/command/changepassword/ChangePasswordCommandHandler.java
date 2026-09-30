package vn.edu.fsoftacademy.api.application.command.changepassword;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

/** Changes the password of the authenticated user and invalidates all active sessions. */
public class ChangePasswordCommandHandler {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final SessionTokenPort sessionTokenPort;

  public ChangePasswordCommandHandler(
      UserRepository userRepository,
      PasswordHasher passwordHasher,
      SessionTokenPort sessionTokenPort) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.sessionTokenPort = sessionTokenPort;
  }

  public void execute(UUID userId, ChangePasswordCommand command) {
    var currentUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!passwordHasher.matches(command.currentPassword(), currentUser.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    String newPasswordHash = passwordHasher.hash(command.newPassword());
    currentUser.updatePasswordHash(newPasswordHash);
    userRepository.save(currentUser);
    sessionTokenPort.revokeAll(currentUser.getId());
  }
}
