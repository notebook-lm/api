package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class User {
  private final UUID id;
  private String email;
  private String displayName;
  private String passwordHash;
  private final boolean enabled;
  private final Instant createdAt;
  private Instant updatedAt;
  private final Set<Role> roles;

  public User(String email, String displayName, String passwordHash) {
    this(
        UUID.randomUUID(),
        email,
        displayName,
        passwordHash,
        true,
        Instant.now(),
        Instant.now(),
        Set.of());
  }

  public User(
      UUID id,
      String email,
      String displayName,
      String passwordHash,
      boolean enabled,
      Instant createdAt,
      Instant updatedAt) {
    this(id, email, displayName, passwordHash, enabled, createdAt, updatedAt, Set.of());
  }

  public User(
      UUID id,
      String email,
      String displayName,
      String passwordHash,
      boolean enabled,
      Instant createdAt,
      Instant updatedAt,
      Set<Role> roles) {
    this.id = id;
    this.email = email;
    this.displayName = displayName;
    this.passwordHash = passwordHash;
    this.enabled = enabled;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.roles = new LinkedHashSet<>(roles);
  }

  public void assignRole(Role role) {
    roles.add(role);
  }

  public void updateDisplayName(String value) {
    displayName = value;
    touch();
  }

  public void updateEmail(String value) {
    email = value;
    touch();
  }

  public void updatePasswordHash(String value) {
    passwordHash = value;
    touch();
  }

  private void touch() {
    updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Set<Role> getRoles() {
    return Set.copyOf(roles);
  }

  public Set<String> getRoleNames() {
    return roles.stream()
        .map(Role::getName)
        .collect(java.util.stream.Collectors.toUnmodifiableSet());
  }

  public Set<String> getPermissionCodes() {
    return roles.stream()
        .flatMap(role -> role.getPermissionCodes().stream())
        .collect(java.util.stream.Collectors.toUnmodifiableSet());
  }
}
