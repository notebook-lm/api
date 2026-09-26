package vn.edu.fsoftacademy.api.application.mapper.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;
import vn.edu.fsoftacademy.api.domain.event.DocumentUploadedEvent;

@Component
public class DocumentUploadedOutboxEventMapper {
  private static final String EVENT_TYPE = "document.uploaded";

  private final ObjectMapper objectMapper;

  public DocumentUploadedOutboxEventMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public OutboxEvent toOutboxEvent(DocumentUploadedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(Map.of(
          "eventId", event.id(),
          "eventType", EVENT_TYPE,
          "occurredAt", event.occurredAt().toString(),
          "data", Map.of(
              "documentId", event.documentId(),
              "projectId", event.projectId(),
              "objectKey", event.objectKey(),
              "originalFilename", event.originalFilename(),
              "contentType", event.contentType(),
              "sizeBytes", event.sizeBytes())));
      return new OutboxEvent(
          event.id(), EVENT_TYPE, EVENT_TYPE, payload, 0,
          event.occurredAt(), event.occurredAt(), null, null);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Could not serialize document uploaded event", ex);
    }
  }
}
