package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public record UploadDocumentResult(
    UUID id,
    UUID projectId,
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    DocumentProcessingStatus status,
    Instant createdAt,
    Instant updatedAt) {}
