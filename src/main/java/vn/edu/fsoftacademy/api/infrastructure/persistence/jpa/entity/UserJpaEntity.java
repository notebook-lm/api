package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {
  @Id private UUID id;
  @Column(nullable = false, unique = true) private String email;
  @Column(name = "display_name", nullable = false) private String displayName;
  @Column(name = "password_hash", nullable = false) private String passwordHash;
  @Column(nullable = false) private boolean enabled;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleJpaEntity> roles = new LinkedHashSet<>();
  protected UserJpaEntity() {}
  public UserJpaEntity(UUID id, String email, String displayName, String passwordHash, boolean enabled,
      Instant createdAt, Instant updatedAt) {
    this(id, email, displayName, passwordHash, enabled, createdAt, updatedAt, Set.of());
  }
  public UserJpaEntity(UUID id, String email, String displayName, String passwordHash, boolean enabled,
      Instant createdAt, Instant updatedAt, Set<RoleJpaEntity> roles) {
    this.id=id; this.email=email; this.displayName=displayName; this.passwordHash=passwordHash; this.enabled=enabled;
    this.createdAt=createdAt; this.updatedAt=updatedAt; this.roles=new LinkedHashSet<>(roles);
  }
  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public String getDisplayName() { return displayName; }
  public String getPasswordHash() { return passwordHash; }
  public boolean isEnabled() { return enabled; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public Set<RoleJpaEntity> getRoles() { return Set.copyOf(roles); }
}
