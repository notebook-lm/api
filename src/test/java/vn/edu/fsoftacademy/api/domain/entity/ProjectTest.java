package vn.edu.fsoftacademy.api.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectTest {
  @Test
  void updateChangesMutableFieldsAndTimestamp() {
    Instant created = Instant.parse("2026-01-01T00:00:00Z");
    var project =
        new Project(UUID.randomUUID(), UUID.randomUUID(), "Initial", null, created, created);

    project.update("Updated", "Description");

    assertEquals("Updated", project.getTitle());
    assertEquals("Description", project.getDescription());
    org.junit.jupiter.api.Assertions.assertTrue(project.getUpdatedAt().isAfter(created));
  }
}
