package vn.edu.fsoftacademy.api.application.query.listprojects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class ListProjectsQueryHandlerTest {
  @Test
  void delegatesAllFiltersAndPagingToOwnerScopedRepository() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    var query =
        new ListProjectsQuery(
            "  notes ",
            Instant.parse("2026-01-01T00:00:00Z"),
            Instant.parse("2026-02-01T00:00:00Z"),
            ProjectSortField.UPDATED_AT,
            SortDirection.ASC,
            1,
            10);
    var project =
        new Project(
            UUID.randomUUID(), ownerId, "Notes", "Description", Instant.now(), Instant.now());
    var expected =
        new vn.edu.fsoftacademy.api.shared.pagination.PageResult<Project>(
            List.of(project), 1, 10, 1, 1, false, true);
    when(projects.findPageByOwnerId(ownerId, query)).thenReturn(expected);

    var result = new ListProjectsQueryHandler(projects).handle(ownerId, query);

    assertEquals(
        List.of("Notes"), result.items().stream().map(Project::getTitle).toList());
    verify(projects).findPageByOwnerId(ownerId, query);
  }

  @Test
  void rejectsInvalidDateRangeBeforeQueryingRepository() {
    var projects = mock(ProjectRepository.class);
    var query =
        new ListProjectsQuery(
            null,
            Instant.parse("2026-02-01T00:00:00Z"),
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            null,
            0,
            20);

    assertThrows(
        IllegalArgumentException.class,
        () -> new ListProjectsQueryHandler(projects).handle(UUID.randomUUID(), query));
    verifyNoInteractions(projects);
  }
}
