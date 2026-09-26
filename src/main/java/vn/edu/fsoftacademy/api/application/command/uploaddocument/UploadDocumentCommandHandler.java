package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.time.Instant;
import java.util.UUID;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.mapper.outbox.DocumentUploadedOutboxEventMapper;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;
import vn.edu.fsoftacademy.api.domain.event.DocumentUploadedEvent;

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
    storage.put(key, command.content(), command.sizeBytes(), command.contentType());
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
        outboxEvents.save(outboxEventMapper.toOutboxEvent(DocumentUploadedEvent.from(saved)));
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
