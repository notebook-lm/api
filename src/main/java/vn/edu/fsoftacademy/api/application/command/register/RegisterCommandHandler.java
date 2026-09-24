package vn.edu.fsoftacademy.api.application.command.register;

import java.util.Locale;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.repository.RoleRepository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

/** Registers a new user account and immediately creates an authenticated session. */
public class RegisterCommandHandler {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordHasher passwordHasher;

  public RegisterCommandHandler(
      UserRepository userRepository, RoleRepository roleRepository, PasswordHasher passwordHasher) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordHasher = passwordHasher;
  }

  public RegisterResult execute(RegisterCommand command) {
    String normalizedEmail = command.email().strip().toLowerCase(Locale.ROOT);
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new ConflictException("An account already exists for this email");
    }

    String displayName = command.displayName().strip();
    String passwordHash = passwordHasher.hash(command.password());
    var newUser = new User(normalizedEmail, displayName, passwordHash);
    newUser.assignRole(
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new IllegalStateException("Default USER role is missing")));
    var savedUser = userRepository.save(newUser);
    return new RegisterResult(savedUser.getId(), savedUser.getEmail(), savedUser.getDisplayName());
  }
}
