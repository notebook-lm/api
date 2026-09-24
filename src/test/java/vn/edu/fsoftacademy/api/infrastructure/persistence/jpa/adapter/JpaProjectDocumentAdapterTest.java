package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.fsoftacademy.api.application.query.listdocuments.DocumentSortDirection;
import vn.edu.fsoftacademy.api.application.query.listdocuments.DocumentSortField;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQuery;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectDocumentJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectDocumentJpaRepository;

class JpaProjectDocumentAdapterTest {
  private final ProjectDocumentJpaRepository repository = mock(ProjectDocumentJpaRepository.class);
  private final JpaProjectDocumentAdapter adapter = new JpaProjectDocumentAdapter(repository);

  @Test
  void mapsOwnerScopedPageToDomainDocuments() {
    var projectId = UUID.randomUUID();
    var entity =
        new ProjectDocumentJpaEntity(
            UUID.randomUUID(),
            projectId,
            "Source",
            "source.pdf",
            "application/pdf",
            5,
            "object-key",
            Instant.now(),
            Instant.now());
    var query =
        new ListDocumentsQuery(
            "source", null, null, DocumentSortField.UPDATED_AT, DocumentSortDirection.ASC, 0, 20);
    when(repository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(entity)));

    var result = adapter.findPageByProjectId(projectId, query);

    assertEquals(List.of("Source"), result.items().stream().map(document -> document.getTitle()).toList());
    assertEquals(List.of(projectId), result.items().stream().map(document -> document.getProjectId()).toList());
    assertEquals(1, result.totalItems());
    verify(repository).findAll(any(Specification.class), any(Pageable.class));
  }
}
