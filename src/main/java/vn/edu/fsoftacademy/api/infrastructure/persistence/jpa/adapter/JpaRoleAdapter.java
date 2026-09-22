package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.RoleRepository;
import vn.edu.fsoftacademy.api.domain.entity.Permission;
import vn.edu.fsoftacademy.api.domain.entity.Role;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RoleJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.RoleJpaRepository;

@Repository
public class JpaRoleAdapter implements RoleRepository {
  private final RoleJpaRepository roles;
  public JpaRoleAdapter(RoleJpaRepository roles) { this.roles = roles; }
  @Override public Optional<Role> findByName(String name) { return roles.findByName(name).map(this::toDomain); }
  private Role toDomain(RoleJpaEntity role) {
    return new Role(role.getId(), role.getName(), role.getPermissions().stream()
        .map(permission -> new Permission(permission.getId(), permission.getCode())).collect(Collectors.toSet()));
  }
}
