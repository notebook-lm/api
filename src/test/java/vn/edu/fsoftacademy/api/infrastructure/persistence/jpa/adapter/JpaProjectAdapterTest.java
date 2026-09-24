package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQuery;
import vn.edu.fsoftacademy.api.application.query.listprojects.ProjectSortField;
import vn.edu.fsoftacademy.api.application.query.listprojects.SortDirection;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectJpaRepository;

class JpaProjectAdapterTest {
  private final ProjectJpaRepository repository = mock(ProjectJpaRepository.class);
  private final JpaProjectAdapter adapter = new JpaProjectAdapter(repository);

  @Test
  void savesAndMapsProjectFields() {
    var project = project();
    adapter.save(project);
    var captor = ArgumentCaptor.forClass(ProjectJpaEntity.class);
    verify(repository).save(captor.capture());
    assertEquals(project.getOwnerId(), captor.getValue().getOwnerId());
    assertEquals(project.getDescription(), captor.getValue().getDescription());
  }

  @Test
  void appliesOwnerScopedPagedQueryAndMapsDomainProject() {
    UUID ownerId = UUID.randomUUID();
    var first = entity(UUID.randomUUID(), ownerId, "Newest");
    var query =
        new ListProjectsQuery(
            "new", null, null, ProjectSortField.UPDATED_AT, SortDirection.DESC, 0, 20);
    when(repository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(first)));

    var result = adapter.findPageByOwnerId(ownerId, query);

    assertEquals(List.of("Newest"), result.items().stream().map(Project::getTitle).toList());
    assertEquals(List.of(ownerId), result.items().stream().map(Project::getOwnerId).toList());
    assertEquals(1, result.totalItems());
    verify(repository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void usesOwnerScopedLookupAndDelete() {
    var project = project();
    when(repository.findByIdAndOwnerId(project.getId(), project.getOwnerId()))
        .thenReturn(Optional.of(entity(project.getId(), project.getOwnerId(), "Notes")));

    assertTrue(adapter.findByIdAndOwnerId(project.getId(), project.getOwnerId()).isPresent());
    adapter.delete(project);
    verify(repository).deleteById(project.getId());
  }

  private Project project() {
    return new Project(
        UUID.randomUUID(), UUID.randomUUID(), "Notes", "Description", Instant.now(), Instant.now());
  }

  private ProjectJpaEntity entity(UUID id, UUID ownerId, String title) {
    return new ProjectJpaEntity(id, ownerId, title, null, Instant.now(), Instant.now());
  }
}
