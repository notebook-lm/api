package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessedfailed;

import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public class DocumentProcessedFailedEventHandler {
  private final ProjectDocumentRepository documents;

  public DocumentProcessedFailedEventHandler(ProjectDocumentRepository documents) {
    this.documents = documents;
  }

  public void handle(DocumentProcessedFailedEvent event) {
    if (!"document.processed.failed".equals(event.eventType())) {
      throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
    }
    var document = documents.findByIdAndProjectId(event.documentId(), event.projectId())
        .orElseThrow(DocumentNotFoundException::new);
    document.updateProcessingStatus(DocumentProcessingStatus.FAILED);
    documents.save(document);
  }
}
