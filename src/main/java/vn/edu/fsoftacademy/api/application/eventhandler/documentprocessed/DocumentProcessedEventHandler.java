package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessed;

import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public class DocumentProcessedEventHandler {
  private final ProjectDocumentRepository documents;

  public DocumentProcessedEventHandler(ProjectDocumentRepository documents) {
    this.documents = documents;
  }

  public void handle(DocumentProcessedEvent event) {
    if (!"document.processed".equals(event.eventType())) {
      throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
    }
    if (event.chunkCount() < 0) throw new IllegalArgumentException("chunkCount must be non-negative");
    var document = documents.findByIdAndProjectId(event.documentId(), event.projectId())
        .orElseThrow(DocumentNotFoundException::new);
    document.updateProcessingStatus(DocumentProcessingStatus.COMPLETED);
    documents.save(document);
  }
}
