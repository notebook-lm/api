package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public class DocumentProcessingEventHandler {
  private final ProjectDocumentRepository documents;

  public DocumentProcessingEventHandler(ProjectDocumentRepository documents) {
    this.documents = documents;
  }

  public void handle(DocumentProcessingEvent event) {
    if (!"document.processing".equals(event.eventType())) {
      throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
    }
    var document = documents.findByIdAndProjectId(event.documentId(), event.projectId())
        .orElseThrow(DocumentNotFoundException::new);
    document.updateProcessingStatus(DocumentProcessingStatus.PROCESSING);
    documents.save(document);
  }
}
