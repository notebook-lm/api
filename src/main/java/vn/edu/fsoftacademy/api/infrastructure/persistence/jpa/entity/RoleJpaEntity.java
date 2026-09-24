package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class RoleJpaEntity {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<PermissionJpaEntity> permissions = new LinkedHashSet<>();

  protected RoleJpaEntity() {}

  public RoleJpaEntity(UUID id, String name, Set<PermissionJpaEntity> permissions) {
    this.id = id;
    this.name = name;
    this.permissions = new LinkedHashSet<>(permissions);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<PermissionJpaEntity> getPermissions() {
    return Set.copyOf(permissions);
  }
}
