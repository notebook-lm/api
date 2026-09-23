package vn.edu.fsoftacademy.api.application.query.currentuser;

import vn.edu.fsoftacademy.api.application.exception.UserNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

/** Loads the profile of the authenticated user without changing application state. */
public class GetCurrentUserQueryHandler {
  private final UserRepository userRepository;

  public GetCurrentUserQueryHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public GetCurrentUserResult handle(GetCurrentUserQuery query) {
    var currentUser = userRepository.findById(query.userId()).orElseThrow(UserNotFoundException::new);
    return new GetCurrentUserResult(currentUser.getId(), currentUser.getEmail(), currentUser.getDisplayName());
  }
}
