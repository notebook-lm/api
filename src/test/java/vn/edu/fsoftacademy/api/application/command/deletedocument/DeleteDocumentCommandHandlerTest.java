package vn.edu.fsoftacademy.api.application.command.deletedocument;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.*;
import vn.edu.fsoftacademy.api.domain.entity.*;

class DeleteDocumentCommandHandlerTest {
  @Test
  void deletesObjectBeforeMetadata() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var storage = mock(ObjectStorage.class);
    var owner = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var document =
        new ProjectDocument(
            documentId,
            projectId,
            "Source",
            "source.pdf",
            "application/pdf",
            1,
            "object-key",
            Instant.now(),
            Instant.now());
    when(projects.findByIdAndOwnerId(projectId, owner))
        .thenReturn(
            Optional.of(new Project(projectId, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(document));
    new DeleteDocumentCommandHandler(projects, documents, storage)
        .execute(owner, projectId, documentId);
    var order = inOrder(storage, documents);
    order.verify(storage).delete("object-key");
    order.verify(documents).delete(document);
  }
}
