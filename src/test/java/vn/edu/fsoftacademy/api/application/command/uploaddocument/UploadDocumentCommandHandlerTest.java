package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fsoftacademy.api.application.mapper.outbox.DocumentUploadedOutboxEventMapper;
import vn.edu.fsoftacademy.api.application.port.JsonMapper;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class UploadDocumentCommandHandlerTest {
  @Test
  void storesFileUnderOpaqueProjectScopedKeyAndPersistsMetadata() {
    var projects = mock(ProjectRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var storage = mock(ObjectStorage.class);
    var outboxEvents = mock(OutboxEventRepository.class);
    var owner = UUID.randomUUID();
    var project = UUID.randomUUID();
    when(projects.findByIdAndOwnerId(project, owner))
        .thenReturn(
            Optional.of(new Project(project, owner, "P", null, Instant.now(), Instant.now())));
    when(documents.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    var transactionManager = mock(PlatformTransactionManager.class);
    var jsonMapper = mock(JsonMapper.class);
    when(transactionManager.getTransaction(any())).thenReturn(mock(TransactionStatus.class));
    when(jsonMapper.write(any())).thenAnswer(invocation -> invocation.getArgument(0).toString());
    var handler = new UploadDocumentCommandHandler(
        projects,
        documents,
        storage,
        outboxEvents,
        new DocumentUploadedOutboxEventMapper(jsonMapper),
        new TransactionTemplate(transactionManager));

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
            eq("application/pdf"),
            eq("source.pdf"));
    verify(documents)
        .save(
            argThat(
                document ->
                    document.getId().equals(result.id())
                        && document.getProjectId().equals(project)
                        && document.getOriginalFilename().equals("source.pdf")));
    verify(outboxEvents).save(argThat(event ->
        event.getEventType().equals("document.uploaded") && event.getPayload().contains(result.id().toString())));
  }
}
