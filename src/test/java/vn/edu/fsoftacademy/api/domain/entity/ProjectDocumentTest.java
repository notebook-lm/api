package vn.edu.fsoftacademy.api.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectDocumentTest {
  @Test
  void renameChangesOnlyTitleAndUpdatedTimestamp() {
    var createdAt = Instant.parse("2026-01-01T00:00:00Z");
    var document =
        new ProjectDocument(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Before",
            "source.pdf",
            "application/pdf",
            10,
            "documents/key",
            createdAt,
            createdAt);

    document.rename("After");

    assertEquals("After", document.getTitle());
    assertEquals("source.pdf", document.getOriginalFilename());
    assertEquals("documents/key", document.getObjectKey());
    assertEquals(createdAt, document.getCreatedAt());
    assertTrue(document.getUpdatedAt().isAfter(createdAt));
  }
}
