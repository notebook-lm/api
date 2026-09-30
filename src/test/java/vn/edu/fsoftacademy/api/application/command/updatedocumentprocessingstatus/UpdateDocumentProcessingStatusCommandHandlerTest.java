package vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class UpdateDocumentProcessingStatusCommandHandlerTest {
  @Test
  void updatesStatusForDocumentInProject() {
    var documents = mock(ProjectDocumentRepository.class);
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var document =
        new ProjectDocument(
            documentId,
            projectId,
            "Document",
            "document.pdf",
            "application/pdf",
            1,
            "key",
            Instant.now(),
            Instant.now());
    when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(document));
    when(documents.save(document)).thenReturn(document);

    var result =
        new UpdateDocumentProcessingStatusCommandHandler(documents)
            .execute(
                new UpdateDocumentProcessingStatusCommand(
                    projectId, documentId, DocumentProcessingStatus.PROCESSING));

    assertEquals(DocumentProcessingStatus.PROCESSING, result.getProcessingStatus());
    verify(documents).save(document);
  }

  @Test
  void rejectsMissingDocument() {
    var documents = mock(ProjectDocumentRepository.class);
    var command =
        new UpdateDocumentProcessingStatusCommand(
            UUID.randomUUID(), UUID.randomUUID(), DocumentProcessingStatus.PROCESSING);
    when(documents.findByIdAndProjectId(command.documentId(), command.projectId()))
        .thenReturn(Optional.empty());

    assertThrows(
        DocumentNotFoundException.class,
        () -> new UpdateDocumentProcessingStatusCommandHandler(documents).execute(command));
  }
}
