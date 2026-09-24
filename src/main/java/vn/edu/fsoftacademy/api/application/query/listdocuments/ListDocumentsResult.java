package vn.edu.fsoftacademy.api.application.query.listdocuments;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListDocumentsResult(
    List<Item> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {
  public record Item(
      UUID id,
      UUID projectId,
      String title,
      String originalFilename,
      String contentType,
      long sizeBytes,
      Instant createdAt,
      Instant updatedAt) {
  }
}
