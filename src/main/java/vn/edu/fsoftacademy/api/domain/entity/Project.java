package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class Project {
  private final UUID id;
  private final UUID ownerId;
  private String title;
  private String description;
  private final Instant createdAt;
  private Instant updatedAt;

  public Project(UUID ownerId, String title, String description) {
    this(UUID.randomUUID(), ownerId, title, description, Instant.now(), Instant.now());
  }

  public Project(UUID id, UUID ownerId, String title, String description, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.ownerId = ownerId;
    this.title = title;
    this.description = description;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public void update(String title, String description) {
    this.title = title;
    this.description = description;
    this.updatedAt = Instant.now();
  }

  public UUID getId() { return id; }
  public UUID getOwnerId() { return ownerId; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
}
