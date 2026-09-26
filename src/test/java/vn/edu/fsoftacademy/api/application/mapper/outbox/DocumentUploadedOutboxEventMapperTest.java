package vn.edu.fsoftacademy.api.application.mapper.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.domain.event.DocumentUploadedEvent;

class DocumentUploadedOutboxEventMapperTest {
  @Test
  void serializesTheCompleteDocumentUploadedPayload() throws Exception {
    var id = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var occurredAt = Instant.parse("2026-01-02T03:04:05Z");
    var event = new DocumentUploadedEvent(id, occurredAt, documentId, projectId, "projects/p/documents/d", "notes.md", "text/markdown", 42);

    var result = new DocumentUploadedOutboxEventMapper(new ObjectMapper()).toOutboxEvent(event);
    var payload = new ObjectMapper().readTree(result.getPayload());

    assertEquals(id, result.getId());
    assertEquals("document.uploaded", result.getEventType());
    assertEquals(0, result.getAttempts());
    assertEquals(occurredAt, result.getCreatedAt());
    assertEquals(occurredAt, result.getNextAttemptAt());
    assertNull(result.getPublishedAt());
    assertNull(result.getLastError());
    assertEquals(id.toString(), payload.path("eventId").asText());
    assertEquals("document.uploaded", payload.path("eventType").asText());
    assertEquals(documentId.toString(), payload.path("data").path("documentId").asText());
    assertEquals(projectId.toString(), payload.path("data").path("projectId").asText());
    assertEquals("projects/p/documents/d", payload.path("data").path("objectKey").asText());
    assertEquals("notes.md", payload.path("data").path("originalFilename").asText());
    assertEquals("text/markdown", payload.path("data").path("contentType").asText());
    assertEquals(42, payload.path("data").path("sizeBytes").asLong());
  }
}
