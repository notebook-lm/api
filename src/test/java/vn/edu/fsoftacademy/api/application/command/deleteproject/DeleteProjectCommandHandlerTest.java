package vn.edu.fsoftacademy.api.application.command.deleteproject;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class DeleteProjectCommandHandlerTest {
  @Test
  void deletesProjectOnlyAfterOwnerScopedLookup() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    var project = new Project(projectId, ownerId, "Notes", null, Instant.now(), Instant.now());
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.of(project));

    new DeleteProjectCommandHandler(projects).execute(ownerId, projectId);

    verify(projects).delete(project);
  }

  @Test
  void doesNotDeleteInaccessibleProject() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.empty());

    assertThrows(ProjectNotFoundException.class,
        () -> new DeleteProjectCommandHandler(projects).execute(ownerId, projectId));
    verify(projects, never()).delete(any());
  }
}
