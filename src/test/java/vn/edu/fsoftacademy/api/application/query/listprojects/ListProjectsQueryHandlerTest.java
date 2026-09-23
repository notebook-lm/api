package vn.edu.fsoftacademy.api.application.query.listprojects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class ListProjectsQueryHandlerTest {
  @Test
  void returnsRepositoryOrderForAuthenticatedOwner() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    Instant now = Instant.now();
    var newest = new Project(UUID.randomUUID(), ownerId, "Newest", null, now, now);
    var oldest = new Project(UUID.randomUUID(), ownerId, "Oldest", null, now.minusSeconds(1), now);
    when(projects.findAllByOwnerId(ownerId)).thenReturn(List.of(newest, oldest));

    var result = new ListProjectsQueryHandler(projects).handle(ownerId);

    assertEquals(List.of("Newest", "Oldest"), result.stream().map(ListProjectsResult::title).toList());
    verify(projects).findAllByOwnerId(ownerId);
  }
}
