package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class RefreshSession {
  private final UUID id;
  private final UUID userId;
  private final String tokenHash;
  private final Instant expiresAt;
  private Instant revokedAt;
  private final Instant createdAt;

  public RefreshSession(UUID userId, String tokenHash, Instant expiresAt) {
    this(UUID.randomUUID(), userId, tokenHash, expiresAt, null, Instant.now());
  }

  public RefreshSession(
      UUID id,
      UUID userId,
      String tokenHash,
      Instant expiresAt,
      Instant revokedAt,
      Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.revokedAt = revokedAt;
    this.createdAt = createdAt;
  }

  public boolean isActive() {
    return revokedAt == null && expiresAt.isAfter(Instant.now());
  }

  public void revoke() {
    revokedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
