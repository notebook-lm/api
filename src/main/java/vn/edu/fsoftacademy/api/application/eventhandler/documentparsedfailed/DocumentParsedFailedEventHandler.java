package vn.edu.fsoftacademy.api.application.eventhandler.documentparsedfailed;

import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public class DocumentParsedFailedEventHandler {
  private final ProjectDocumentRepository documents;

  public DocumentParsedFailedEventHandler(ProjectDocumentRepository documents) {
    this.documents = documents;
  }

  public void handle(DocumentParsedFailedEvent event) {
    if (!"document.parsed.failed".equals(event.eventType())) {
      throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
    }
    var document = documents.findByIdAndProjectId(event.documentId(), event.projectId())
        .orElseThrow(DocumentNotFoundException::new);
    document.updateProcessingStatus(DocumentProcessingStatus.FAILED);
    documents.save(document);
  }
}
