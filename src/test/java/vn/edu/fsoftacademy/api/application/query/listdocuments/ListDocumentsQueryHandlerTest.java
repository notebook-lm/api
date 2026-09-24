package vn.edu.fsoftacademy.api.application.query.listdocuments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.*;
import vn.edu.fsoftacademy.api.domain.entity.*;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

class ListDocumentsQueryHandlerTest {
  @Test
  void listsPagedDocumentsOnlyAfterOwnerScopedProjectLookup() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    var query = new ListDocumentsQuery(null, null, null, null, null, 0, 20);
    var document = document(project);
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.findPageByProjectId(project, query))
        .thenReturn(new PageResult<>(List.of(document), 0, 20, 1, 1, false, false));

    var result = new ListDocumentsQueryHandler(projects, documents).handle(owner, project, query);

    assertEquals(List.of("Source"), result.items().stream().map(ListDocumentsResult.Item::title).toList());
    verify(documents).findPageByProjectId(project, query);
  }

  @Test
  void rejectsInvalidDateRangeBeforeAnyLookup() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var query = new ListDocumentsQuery(null, Instant.parse("2026-02-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"), null, null, 0, 20);

    assertThrows(IllegalArgumentException.class, () -> new ListDocumentsQueryHandler(projects, documents).handle(UUID.randomUUID(), UUID.randomUUID(), query));
    verifyNoInteractions(projects, documents);
  }

  private ProjectDocument document(UUID projectId) {
    return new ProjectDocument(UUID.randomUUID(), projectId, "Source", "source.pdf", "application/pdf", 5, "object-key", Instant.now(), Instant.now());
  }
}
