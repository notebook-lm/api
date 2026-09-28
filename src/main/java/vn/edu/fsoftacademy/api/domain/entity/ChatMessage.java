package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class ChatMessage {
  private final UUID id;
  private final UUID conversationId;
  private final ChatMessageRole role;
  private String content;
  private ChatMessageStatus status;
  private final String provider;
  private final Instant createdAt;
  private Instant updatedAt;

  public ChatMessage(UUID conversationId, ChatMessageRole role, String content, ChatMessageStatus status,
      String provider) {
    this(UUID.randomUUID(), conversationId, role, content, status, provider, Instant.now(), Instant.now());
  }

  public ChatMessage(UUID id, UUID conversationId, ChatMessageRole role, String content, ChatMessageStatus status,
      String provider, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.conversationId = conversationId;
    this.role = role;
    this.content = content;
    this.status = status;
    this.provider = provider;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public void append(String delta) {
    this.content += delta;
    this.updatedAt = Instant.now();
  }

  public void complete() {
    this.status = ChatMessageStatus.COMPLETED;
    this.updatedAt = Instant.now();
  }

  public void fail() {
    this.status = ChatMessageStatus.FAILED;
    this.updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public ChatMessageRole getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  public ChatMessageStatus getStatus() {
    return status;
  }

  public String getProvider() {
    return provider;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
