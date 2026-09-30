package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshSessionJpaEntity {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected RefreshSessionJpaEntity() {}

  public RefreshSessionJpaEntity(
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
