package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListProjectsResult(
    List<Item> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {
  public record Item(
      UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
}
