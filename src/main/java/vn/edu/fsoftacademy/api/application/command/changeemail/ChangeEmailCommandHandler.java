package vn.edu.fsoftacademy.api.application.command.changeemail;

import java.util.Locale;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.exception.InvalidCredentialsException;
import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

/** Changes the email address of the authenticated user and invalidates all active sessions. */
public class ChangeEmailCommandHandler {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final SessionTokenPort sessionTokenPort;

  public ChangeEmailCommandHandler(
      UserRepository userRepository,
      PasswordHasher passwordHasher,
      SessionTokenPort sessionTokenPort) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.sessionTokenPort = sessionTokenPort;
  }

  public ChangeEmailResult execute(UUID userId, ChangeEmailCommand command) {
    var currentUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!passwordHasher.matches(command.currentPassword(), currentUser.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    String normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);
    boolean isEmailChanged = !currentUser.getEmail().equals(normalizedEmail);

    if (isEmailChanged && userRepository.existsByEmail(normalizedEmail)) {
      throw new ConflictException("An account already exists for this email");
    }

    currentUser.updateEmail(normalizedEmail);
    userRepository.save(currentUser);
    sessionTokenPort.revokeAll(currentUser.getId());

    return new ChangeEmailResult(
        currentUser.getId(), currentUser.getEmail(), currentUser.getDisplayName());
  }
}
