package vn.edu.fsoftacademy.api.application.event.documentuploaded;

import java.time.Instant;
import java.util.UUID;

public record DocumentUploadedEvent(
    UUID id,
    Instant occurredAt,
    UUID userId,
    UUID documentId,
    UUID projectId,
    String objectKey,
    String originalFilename,
    String contentType,
    long sizeBytes) {}
