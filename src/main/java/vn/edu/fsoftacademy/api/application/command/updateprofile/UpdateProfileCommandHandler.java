package vn.edu.fsoftacademy.api.application.command.updateprofile;

import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

/** Updates the display name of the authenticated user. */
public class UpdateProfileCommandHandler {
  private final UserRepository userRepository;

  public UpdateProfileCommandHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UpdateProfileResult execute(UUID userId, UpdateProfileCommand command) {
    var currentUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    String displayName = command.displayName().strip();

    currentUser.updateDisplayName(displayName);
    userRepository.save(currentUser);

    return new UpdateProfileResult(currentUser.getId(), currentUser.getEmail(), currentUser.getDisplayName());
  }
}
