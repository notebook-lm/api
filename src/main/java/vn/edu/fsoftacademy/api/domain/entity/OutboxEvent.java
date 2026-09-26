package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class OutboxEvent {
  private final UUID id;
  private final String eventType;
  private final String topic;
  private final String payload;
  private final int attempts;
  private final Instant nextAttemptAt;
  private final Instant createdAt;
  private final Instant publishedAt;
  private final String lastError;

  public OutboxEvent(UUID id, String eventType, String topic, String payload, int attempts, Instant nextAttemptAt,
      Instant createdAt, Instant publishedAt, String lastError) {
    this.id = id;
    this.eventType = eventType;
    this.topic = topic;
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

  public String getTopic() {
    return topic;
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
}
