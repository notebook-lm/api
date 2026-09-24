package vn.edu.fsoftacademy.api.application.query.getdocument;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.*;
import vn.edu.fsoftacademy.api.domain.entity.*;

class GetDocumentQueryHandlerTest {
  @Test
  void returnsOnlyDocumentInOwnedProject() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    var id = UUID.randomUUID();
    var document =
        new ProjectDocument(
            id, project, "D", "d.txt", "text/plain", 1, "key", Instant.now(), Instant.now());
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(
            Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.findByIdAndProjectId(id, project)).thenReturn(Optional.of(document));
    assertSame(
        document, new GetDocumentQueryHandler(projects, documents).handle(owner, project, id));
  }
}
