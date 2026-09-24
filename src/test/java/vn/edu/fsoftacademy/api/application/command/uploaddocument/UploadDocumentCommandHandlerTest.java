package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.*;
import vn.edu.fsoftacademy.api.domain.entity.Project;

class UploadDocumentCommandHandlerTest {
  @Test
  void storesFileUnderOpaqueProjectScopedKeyAndPersistsMetadata() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var storage = mock(ObjectStorage.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(
            Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    var handler = new UploadDocumentCommandHandler(projects, documents, storage);
    var result =
        handler.execute(
            owner,
            project,
            new UploadDocumentCommand(
                "Source",
                "source.pdf",
                "application/pdf",
                3,
                new ByteArrayInputStream(new byte[] {1, 2, 3})));
    assertEquals("Source", result.title());
    verify(storage)
        .put(
            matches("projects/" + project + "/documents/[0-9a-f-]+"),
            any(),
            eq(3L),
            eq("application/pdf"));
    verify(documents)
        .save(
            argThat(
                d ->
                    d.getId().equals(result.id())
                        && d.getProjectId().equals(project)
                        && d.getOriginalFilename().equals("source.pdf")));
  }
}
