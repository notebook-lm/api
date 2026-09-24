package vn.edu.fsoftacademy.api.application.command.createproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class CreateProjectCommandHandlerTest {
  @Test
  void createsProjectForCallerAndNormalizesTitle() {
    var projects = mock(ProjectRepository.class);
    UUID ownerId = UUID.randomUUID();
    when(projects.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = new CreateProjectCommandHandler(projects)
        .execute(ownerId, new CreateProjectCommand("  Research notes  ", "Sources"));

    ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
    verify(projects).save(captor.capture());
    assertEquals(ownerId, captor.getValue().getOwnerId());
    assertEquals("Research notes", captor.getValue().getTitle());
    assertEquals("Sources", result.description());
  }
}
