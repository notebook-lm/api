package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class DocumentProcessingEventHandlerTest {
  @Test void marksDocumentAsProcessing() {
    var documents = mock(ProjectDocumentRepository.class); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID(); var document = mock(ProjectDocument.class);
    when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(document));
    new DocumentProcessingEventHandler(documents).handle(new DocumentProcessingEvent(UUID.randomUUID(), "document.processing", UUID.randomUUID(), projectId, documentId));
    verify(documents).save(document);
  }
  @Test void rejectsUnexpectedEventType() {
    assertThrows(IllegalArgumentException.class, () -> new DocumentProcessingEventHandler(mock(ProjectDocumentRepository.class)).handle(new DocumentProcessingEvent(UUID.randomUUID(), "document.processed", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())));
  }
}
