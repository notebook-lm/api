package vn.edu.fsoftacademy.api.application.command.updatedocument;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.DocumentNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class UpdateDocumentCommandHandlerTest {
  @Test
  void renamesOnlyDocumentWithinOwnedProject() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var project = new Project(projectId, owner, "Project", null, Instant.now(), Instant.now());
    var document =
        new ProjectDocument(
            documentId,
            projectId,
            "Old",
            "x.pdf",
            "application/pdf",
            1,
            "key",
            Instant.now(),
            Instant.now());
    when(projects.findByIdAndOwnerId(projectId, owner)).thenReturn(Optional.of(project));
    when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(document));
    when(documents.save(document)).thenReturn(document);

    var result =
        new UpdateDocumentCommandHandler(projects, documents)
            .execute(owner, projectId, documentId, new UpdateDocumentCommand("New"));

    assertEquals("New", result.getTitle());
    verify(documents).save(document);
  }

  @Test
  void rejectsMissingProjectBeforeDocumentLookup() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(project, owner)).thenReturn(Optional.empty());
    assertThrows(
        ProjectNotFoundException.class,
        () ->
            new UpdateDocumentCommandHandler(projects, documents)
                .execute(owner, project, UUID.randomUUID(), new UpdateDocumentCommand("New")));
    verifyNoInteractions(documents);
  }

  @Test
  void rejectsDocumentOutsideProject() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    var document = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(
            Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.findByIdAndProjectId(document, project)).thenReturn(Optional.empty());
    assertThrows(
        DocumentNotFoundException.class,
        () ->
            new UpdateDocumentCommandHandler(projects, documents)
                .execute(owner, project, document, new UpdateDocumentCommand("New")));
  }
}
