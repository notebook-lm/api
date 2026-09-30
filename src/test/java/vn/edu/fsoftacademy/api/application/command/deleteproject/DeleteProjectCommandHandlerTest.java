package vn.edu.fsoftacademy.api.application.command.deleteproject;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class DeleteProjectCommandHandlerTest {
  @Test
  void deletesDocumentObjectsBeforeDeletingOwnerScopedProject() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var storage = mock(ObjectStorage.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    var project = new Project(projectId, ownerId, "Notes", null, Instant.now(), Instant.now());
    var document =
        new ProjectDocument(
            projectId, "Source", "source.pdf", "application/pdf", 42, "projects/key");
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.of(project));
    when(documents.findAllByProjectId(projectId)).thenReturn(List.of(document));

    new DeleteProjectCommandHandler(projects, documents, storage).execute(ownerId, projectId);

    var order = inOrder(storage, projects);
    order.verify(storage).delete("projects/key");
    order.verify(projects).delete(project);
  }

  @Test
  void doesNotDeleteInaccessibleProject() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var storage = mock(ObjectStorage.class);
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.empty());

    assertThrows(
        ProjectNotFoundException.class,
        () ->
            new DeleteProjectCommandHandler(projects, documents, storage)
                .execute(ownerId, projectId));
    verifyNoInteractions(documents, storage);
    verify(projects, never()).delete(any());
  }
}
