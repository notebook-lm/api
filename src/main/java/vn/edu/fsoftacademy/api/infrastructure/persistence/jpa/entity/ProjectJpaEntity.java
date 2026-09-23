package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class ProjectJpaEntity {
  @Id private UUID id;
  @Column(name = "owner_id", nullable = false) private UUID ownerId;
  @Column(nullable = false) private String title;
  @Column private String description;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;
  protected ProjectJpaEntity() {}
  public ProjectJpaEntity(UUID id, UUID ownerId, String title, String description, Instant createdAt, Instant updatedAt) {
    this.id = id; this.ownerId = ownerId; this.title = title; this.description = description;
    this.createdAt = createdAt; this.updatedAt = updatedAt;
  }
  public UUID getId() { return id; }
  public UUID getOwnerId() { return ownerId; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
}
