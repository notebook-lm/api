package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

@Entity
@Table(name = "project_documents")
public class ProjectDocumentJpaEntity {
  @Id private UUID id;

  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @Column(nullable = false)
  private String title;

  @Column(name = "original_filename", nullable = false)
  private String originalFilename;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "object_key", nullable = false)
  private String objectKey;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "processing_status", nullable = false)
  private DocumentProcessingStatus processingStatus;

  protected ProjectDocumentJpaEntity() {}

  public ProjectDocumentJpaEntity(
      UUID id,
      UUID projectId,
      String title,
      String originalFilename,
      String contentType,
      long sizeBytes,
      String objectKey,
      Instant createdAt,
      Instant updatedAt,
      DocumentProcessingStatus processingStatus) {
    this.id = id;
    this.projectId = projectId;
    this.title = title;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.objectKey = objectKey;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.processingStatus = processingStatus;
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

  public String getOriginalFilename() {
    return originalFilename;
  }

  public String getContentType() {
    return contentType;
  }

  public long getSizeBytes() {
    return sizeBytes;
  }

  public String getObjectKey() {
    return objectKey;
  }

  public DocumentProcessingStatus getProcessingStatus() { return processingStatus; }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
