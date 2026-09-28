package vn.edu.fsoftacademy.api.api.rest.document.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProjectDocumentResponse(
    UUID id,
    UUID projectId,
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    String status,
    Instant createdAt,
    Instant updatedAt) {}
