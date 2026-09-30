package vn.edu.fsoftacademy.api.application.command.deleteaccount;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

/** Deletes the authenticated user's sessions and account after password verification. */
public class DeleteAccountCommandHandler {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final SessionTokenPort sessionTokenPort;

  public DeleteAccountCommandHandler(
      UserRepository userRepository,
      PasswordHasher passwordHasher,
      SessionTokenPort sessionTokenPort) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.sessionTokenPort = sessionTokenPort;
  }

  public void execute(UUID userId, DeleteAccountCommand command) {
    var currentUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!passwordHasher.matches(command.currentPassword(), currentUser.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    sessionTokenPort.deleteAll(currentUser.getId());
    userRepository.delete(currentUser);
  }
}
