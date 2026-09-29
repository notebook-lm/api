package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.time.Instant;
import java.util.UUID;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fsoftacademy.api.application.event.documentuploaded.DocumentUploadedEvent;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.mapper.outbox.DocumentUploadedOutboxEventMapper;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public class UploadDocumentCommandHandler {
  private final ProjectRepository projects;
  private final ProjectDocumentRepository documents;
  private final ObjectStorage storage;
  private final OutboxEventRepository outboxEvents;
  private final DocumentUploadedOutboxEventMapper outboxEventMapper;
  private final TransactionTemplate transactionTemplate;

  public UploadDocumentCommandHandler(
      ProjectRepository projects,
      ProjectDocumentRepository documents,
      ObjectStorage storage,
      OutboxEventRepository outboxEvents,
      DocumentUploadedOutboxEventMapper outboxEventMapper,
      TransactionTemplate transactionTemplate) {
    this.projects = projects;
    this.documents = documents;
    this.storage = storage;
    this.outboxEvents = outboxEvents;
    this.outboxEventMapper = outboxEventMapper;
    this.transactionTemplate = transactionTemplate;
  }

  public UploadDocumentResult execute(UUID ownerId, UUID projectId, UploadDocumentCommand command) {
    projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
    var id = UUID.randomUUID();
    var key = "projects/" + projectId + "/documents/" + id;
    storage.put(
        key,
        command.content(),
        command.sizeBytes(),
        command.contentType(),
        command.originalFilename());
    try {
      var document = transactionTemplate.execute(status -> {
        Instant now = Instant.now();
        var saved = documents.save(
            new ProjectDocument(
                id,
                projectId,
                command.title(),
                command.originalFilename(),
                command.contentType(),
                command.sizeBytes(),
                key,
                now,
                now));
        var event = new DocumentUploadedEvent(
            UUID.randomUUID(),
            Instant.now(),
            ownerId,
            saved.getId(),
            saved.getProjectId(),
            saved.getObjectKey(),
            saved.getOriginalFilename(),
            saved.getContentType(),
            saved.getSizeBytes());
        var payload = outboxEventMapper.toPayload(event);
        var outboxEvent = new OutboxEvent(
            event.id(),
            "document.uploaded",
            "document.uploaded",
            payload,
            0,
            event.occurredAt(),
            event.occurredAt(),
            null,
            null);
        outboxEvents.save(outboxEvent);
        return saved;
      });
      if (document == null) {
        throw new IllegalStateException("Could not persist uploaded document");
      }
      return new UploadDocumentResult(
          document.getId(),
          document.getProjectId(),
          document.getTitle(),
          document.getOriginalFilename(),
          document.getContentType(),
          document.getSizeBytes(),
          document.getProcessingStatus(),
          document.getCreatedAt(),
          document.getUpdatedAt());
    } catch (RuntimeException ex) {
      try {
        storage.delete(key);
      } catch (RuntimeException ignored) {
      }
      throw ex;
    }
  }

}
