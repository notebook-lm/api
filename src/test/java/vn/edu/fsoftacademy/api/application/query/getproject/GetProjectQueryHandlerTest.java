package vn.edu.fsoftacademy.api.application.query.getproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class GetProjectQueryHandlerTest {
  @Test
  void returnsOnlyProjectOwnedByCaller() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.of(
        new Project(projectId, ownerId, "Notes", "Description", Instant.now(), Instant.now())));

    var result = new GetProjectQueryHandler(projects).handle(ownerId, projectId);

    assertEquals("Notes", result.title());
    verify(projects).findByIdAndOwnerId(projectId, ownerId);
  }

  @Test
  void reportsMissingAndOtherOwnerProjectsAsNotFound() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.empty());

    assertThrows(ProjectNotFoundException.class,
        () -> new GetProjectQueryHandler(projects).handle(ownerId, projectId));
  }
}
