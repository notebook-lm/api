package vn.edu.fsoftacademy.api.application.command.updateproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class UpdateProjectCommandHandlerTest {
  @Test
  void updatesOnlyProjectOwnedByCaller() {
    var repo = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    var project =
        new Project(
            projectId, ownerId, "Old", null, java.time.Instant.now(), java.time.Instant.now());
    when(repo.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.of(project));

    var result =
        new UpdateProjectCommandHandler(repo)
            .execute(ownerId, projectId, new UpdateProjectCommand("  New  ", "Details"));

    assertEquals("New", result.title());
    assertEquals("Details", result.description());
    verify(repo).save(project);
  }

  @Test
  void hidesProjectsOutsideCallersOwnership() {
    var repo = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    when(repo.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.empty());

    assertThrows(
        ProjectNotFoundException.class,
        () ->
            new UpdateProjectCommandHandler(repo)
                .execute(ownerId, projectId, new UpdateProjectCommand("Project", null)));
    verify(repo, never()).save(any());
  }
}
