package vn.edu.fsoftacademy.api.api.rest.document.dto.response;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public record ProjectDocumentResponse(
    UUID id,
    UUID projectId,
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    DocumentProcessingStatus status,
    Instant createdAt,
    Instant updatedAt) {}
