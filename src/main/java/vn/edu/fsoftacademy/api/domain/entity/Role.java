package vn.edu.fsoftacademy.api.domain.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Role {
  private final UUID id;
  private final String name;
  private final Set<Permission> permissions;

  public Role(String name) {
    this(UUID.randomUUID(), name, Set.of());
  }

  public Role(UUID id, String name, Set<Permission> permissions) {
    this.id = id;
    this.name = name;
    this.permissions = Set.copyOf(permissions);
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public Set<Permission> getPermissions() { return permissions; }

  public Set<String> getPermissionCodes() {
    Set<String> codes = new LinkedHashSet<>();
    permissions.forEach(permission -> codes.add(permission.code()));
    return Set.copyOf(codes);
  }
}
