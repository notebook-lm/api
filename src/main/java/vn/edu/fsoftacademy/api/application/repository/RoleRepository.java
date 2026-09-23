package vn.edu.fsoftacademy.api.application.repository;

import java.util.Optional;
import vn.edu.fsoftacademy.api.domain.entity.Role;

public interface RoleRepository {
  Optional<Role> findByName(String name);
}
