package vn.edu.fsoftacademy.api.application.query.listdocuments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.*;
import vn.edu.fsoftacademy.api.domain.entity.*;

class ListDocumentsQueryHandlerTest {
  @Test
  void listsDocumentsOnlyAfterOwnerScopedProjectLookup() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(
            Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.findAllByProjectId(project)).thenReturn(List.of());
    assertTrue(new ListDocumentsQueryHandler(projects, documents).handle(owner, project).isEmpty());
    verify(documents).findAllByProjectId(project);
  }
}
