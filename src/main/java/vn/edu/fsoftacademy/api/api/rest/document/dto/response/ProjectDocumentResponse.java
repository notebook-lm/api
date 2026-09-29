package vn.edu.fsoftacademy.api.api.rest.document.dto.response;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public record ProjectDocumentResponse(
    UUID id,
    UUID projectId,
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    String status,
    Instant createdAt,
    Instant updatedAt) {
  public static ProjectDocumentResponse from(ProjectDocument document) {
    return new ProjectDocumentResponse(
        document.getId(), document.getProjectId(), document.getTitle(), document.getOriginalFilename(),
        document.getContentType(), document.getSizeBytes(), document.getProcessingStatus().name(),
        document.getCreatedAt(), document.getUpdatedAt());
  }
}
