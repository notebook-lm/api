package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.time.Instant;
import java.util.UUID;

public record UploadDocumentResult(
    UUID id,
    UUID projectId,
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    Instant createdAt,
    Instant updatedAt) {}
