package vn.edu.fsoftacademy.api.application.event.documentuploaded;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public record DocumentUploadedEvent(
    UUID id,
    Instant occurredAt,
    UUID documentId,
    UUID projectId,
    String objectKey,
    String originalFilename,
    String contentType,
    long sizeBytes) {
  public static DocumentUploadedEvent from(ProjectDocument document) {
    return new DocumentUploadedEvent(
        UUID.randomUUID(),
        Instant.now(),
        document.getId(),
        document.getProjectId(),
        document.getObjectKey(),
        document.getOriginalFilename(),
        document.getContentType(),
        document.getSizeBytes());
  }
}
