package vn.edu.fsoftacademy.api.application.mapper.outbox;

import java.util.Map;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.event.documentuploaded.DocumentUploadedEvent;
import vn.edu.fsoftacademy.api.application.port.JsonMapper;

@Component
public class DocumentUploadedOutboxEventMapper {
  private static final String EVENT_TYPE = "document.uploaded";

  private final JsonMapper jsonMapper;

  public DocumentUploadedOutboxEventMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public String toPayload(DocumentUploadedEvent event) {
    return jsonMapper.write(
        Map.of(
            "eventId", event.id(),
            "eventType", EVENT_TYPE,
            "occurredAt", event.occurredAt().toString(),
            "data",
                Map.of(
                    "userId", event.userId(),
                    "documentId", event.documentId(),
                    "projectId", event.projectId(),
                    "objectKey", event.objectKey(),
                    "originalFilename", event.originalFilename(),
                    "contentType", event.contentType(),
                    "sizeBytes", event.sizeBytes())));
  }
}
