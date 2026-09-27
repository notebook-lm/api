package vn.edu.fsoftacademy.api.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class ProjectDocument {
  private final UUID id;
  private final UUID projectId;
  private String title;
  private final String originalFilename;
  private final String contentType;
  private final long sizeBytes;
  private final String objectKey;
  private final Instant createdAt;
  private Instant updatedAt;
  private DocumentProcessingStatus processingStatus;

  public ProjectDocument(
      UUID projectId,
      String title,
      String originalFilename,
      String contentType,
      long sizeBytes,
      String objectKey) {
    this(
        UUID.randomUUID(),
        projectId,
        title,
        originalFilename,
        contentType,
        sizeBytes,
        objectKey,
        Instant.now(),
        Instant.now(),
        DocumentProcessingStatus.PENDING);
  }

  public ProjectDocument(
      UUID id,
      UUID projectId,
      String title,
      String originalFilename,
      String contentType,
      long sizeBytes,
      String objectKey,
      Instant createdAt,
      Instant updatedAt) {
    this(id, projectId, title, originalFilename, contentType, sizeBytes, objectKey, createdAt, updatedAt, DocumentProcessingStatus.PENDING);
  }

  public ProjectDocument(
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

  public void rename(String title) {
    this.title = title;
    this.updatedAt = Instant.now();
  }

  public void updateProcessingStatus(DocumentProcessingStatus processingStatus) {
    this.processingStatus = processingStatus;
    this.updatedAt = Instant.now();
  }

  public DocumentProcessingStatus getProcessingStatus() {
    return processingStatus;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
