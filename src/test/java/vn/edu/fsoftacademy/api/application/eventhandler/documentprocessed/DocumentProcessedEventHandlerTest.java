package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessed;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class DocumentProcessedEventHandlerTest {
  @Test void marksDocumentAsCompleted() {
    var documents = mock(ProjectDocumentRepository.class); var projectId = UUID.randomUUID(); var documentId = UUID.randomUUID(); var document = mock(ProjectDocument.class);
    when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(document));
    new DocumentProcessedEventHandler(documents).handle(new DocumentProcessedEvent(UUID.randomUUID(), "document.processed", UUID.randomUUID(), projectId, documentId, 2));
    verify(documents).save(document);
  }
  @Test void rejectsNegativeChunkCount() {
    assertThrows(IllegalArgumentException.class, () -> new DocumentProcessedEventHandler(mock(ProjectDocumentRepository.class)).handle(new DocumentProcessedEvent(UUID.randomUUID(), "document.processed", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), -1)));
  }
}
