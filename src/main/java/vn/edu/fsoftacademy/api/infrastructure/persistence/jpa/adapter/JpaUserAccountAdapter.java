package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.Permission;
import vn.edu.fsoftacademy.api.domain.entity.Role;
import vn.edu.fsoftacademy.api.domain.entity.User;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.PermissionJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RoleJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.UserJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.UserJpaRepository;

@Repository
public class JpaUserAccountAdapter implements UserRepository {
  private final UserJpaRepository repo;
  public JpaUserAccountAdapter(UserJpaRepository repo) { this.repo = repo; }
  public Optional<User> findById(UUID id) { return repo.findById(id).map(this::toDomain); }
  public Optional<User> findByEmail(String email) { return repo.findByEmail(email).map(this::toDomain); }
  public boolean existsByEmail(String email) { return repo.existsByEmail(email); }
  public User save(User user) { repo.save(toEntity(user)); return user; }
  public void delete(User user) { repo.deleteById(user.getId()); }
  private User toDomain(UserJpaEntity e) {
    return new User(e.getId(), e.getEmail(), e.getDisplayName(), e.getPasswordHash(), e.isEnabled(), e.getCreatedAt(), e.getUpdatedAt(),
        e.getRoles().stream().map(this::toRole).collect(Collectors.toSet()));
  }
  private Role toRole(RoleJpaEntity role) {
    return new Role(role.getId(), role.getName(), role.getPermissions().stream()
        .map(permission -> new Permission(permission.getId(), permission.getCode())).collect(Collectors.toSet()));
  }
  private UserJpaEntity toEntity(User user) {
    return new UserJpaEntity(user.getId(), user.getEmail(), user.getDisplayName(), user.getPasswordHash(), user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt(),
        user.getRoles().stream().map(this::toRoleEntity).collect(Collectors.toSet()));
  }
  private RoleJpaEntity toRoleEntity(Role role) {
    return new RoleJpaEntity(role.getId(), role.getName(), role.getPermissions().stream()
        .map(permission -> new PermissionJpaEntity(permission.id(), permission.code())).collect(Collectors.toSet()));
  }
}
