package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_events")
public class OutboxEventJpaEntity {
  @Id
  private UUID id;
  @Column(name = "event_type", nullable = false)
  private String eventType;
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private String payload;
  @Column(nullable = false)
  private int attempts;
  @Column(name = "next_attempt_at", nullable = false)
  private Instant nextAttemptAt;
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
  @Column(name = "published_at")
  private Instant publishedAt;
  @Column(name = "last_error")
  private String lastError;

  protected OutboxEventJpaEntity() {
  }

  public OutboxEventJpaEntity(UUID id, String eventType, String payload, int attempts,
      Instant nextAttemptAt, Instant createdAt, Instant publishedAt, String lastError) {
    this.id = id;
    this.eventType = eventType;
    this.payload = payload;
    this.attempts = attempts;
    this.nextAttemptAt = nextAttemptAt;
    this.createdAt = createdAt;
    this.publishedAt = publishedAt;
    this.lastError = lastError;
  }

  public UUID getId() {
    return id;
  }

  public String getEventType() {
    return eventType;
  }

  public String getPayload() {
    return payload;
  }

  public int getAttempts() {
    return attempts;
  }

  public Instant getNextAttemptAt() {
    return nextAttemptAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public String getLastError() {
    return lastError;
  }

  public void markPublished(Instant at) {
    publishedAt = at;
    lastError = null;
  }

  public void markFailed(Instant nextAttemptAt, String error) {
    attempts++;
    this.nextAttemptAt = nextAttemptAt;
    lastError = error;
  }
}
