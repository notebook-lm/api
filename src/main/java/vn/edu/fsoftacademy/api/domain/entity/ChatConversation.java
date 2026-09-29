package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class ChatConversation {
  private final UUID id;
  private final UUID projectId;
  private String title;
  private Instant lastMessageAt;
  private final Instant createdAt;
  private Instant updatedAt;

  public ChatConversation(UUID projectId, String title) {
    this(UUID.randomUUID(), projectId, title, null, Instant.now(), Instant.now());
  }

  public ChatConversation(UUID id, UUID projectId, String title, Instant lastMessageAt, Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.projectId = projectId;
    this.title = title;
    this.lastMessageAt = lastMessageAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public void rename(String title) {
    this.title = title;
    this.updatedAt = Instant.now();
  }

  public void touch(Instant timestamp) {
    this.lastMessageAt = timestamp;
    this.updatedAt = timestamp;
  }

  public UUID getId() {
    return id;
  }

  public UUID getProjectId() {
    return projectId;
  }

  public String getTitle() {
    return title;
  }

  public Instant getLastMessageAt() {
    return lastMessageAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
