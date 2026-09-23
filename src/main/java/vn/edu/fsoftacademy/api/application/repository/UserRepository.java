package vn.edu.fsoftacademy.api.application.repository;

import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.User;

public interface UserRepository {
  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  User save(User user);

  void delete(User user);
}
