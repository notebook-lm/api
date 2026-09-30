package vn.edu.fsoftacademy.api.domain.entity;

import java.util.UUID;

public record ChatMessageCitation(
    int citationNumber,
    UUID documentId,
    String filename,
    int chunkIndex,
    String excerpt) {
  public ChatMessageCitation {
    if (citationNumber < 1) throw new IllegalArgumentException("citationNumber must be positive");
    if (documentId == null) throw new IllegalArgumentException("documentId is required");
    if (filename == null || filename.isBlank()) throw new IllegalArgumentException("filename is required");
    if (chunkIndex < 0) throw new IllegalArgumentException("chunkIndex must be non-negative");
    if (excerpt == null || excerpt.isBlank()) throw new IllegalArgumentException("excerpt is required");
    filename = filename.strip();
    excerpt = excerpt.strip();
  }
}
