package vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class DocumentContentExtractedEventHandlerTest {
  @Test
  void storesValidContentAtStableObjectKeyAndSupportsRedelivery() {
    var documents = mock(ProjectDocumentRepository.class);
    var projects = mock(ProjectRepository.class);
    var storage = mock(ObjectStorage.class);
    var transactions = mock(TransactionTemplate.class);
    doAnswer(invocation -> { invocation.getArgument(0, java.util.function.Consumer.class).accept(null); return null; })
        .when(transactions).executeWithoutResult(any());
    var ownerId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    var document = document(documentId, projectId);
    when(documents.findById(documentId)).thenReturn(Optional.of(document));
    when(projects.findByIdAndOwnerId(projectId, ownerId))
        .thenReturn(Optional.of(new Project(projectId, ownerId, "Project", null, Instant.now(), Instant.now())));
    var handler = new DocumentContentExtractedEventHandler(documents, projects, storage, transactions);
    var event = event(documentId, projectId, ownerId, "Nội dung đã parse");

    handler.handle(event);
    handler.handle(event);

    String key = "projects/%s/documents/%s/extracted.txt".formatted(projectId, documentId);
    verify(storage, times(2)).put(eq(key), any(), anyLong(), eq("text/plain; charset=UTF-8"), eq("extracted.txt"));
    verify(documents, times(2)).save(document);
    org.junit.jupiter.api.Assertions.assertEquals(key, document.getExtractedContentObjectKey());
  }

  @Test
  void ignoresMissingDocumentAndOwnershipMismatches() {
    var documents = mock(ProjectDocumentRepository.class);
    var projects = mock(ProjectRepository.class);
    var storage = mock(ObjectStorage.class);
    var transactions = mock(TransactionTemplate.class);
    var handler = new DocumentContentExtractedEventHandler(documents, projects, storage, transactions);
    var ownerId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var documentId = UUID.randomUUID();
    when(documents.findById(documentId)).thenReturn(Optional.empty());
    handler.handle(event(documentId, projectId, ownerId, "text"));

    var mismatched = document(documentId, UUID.randomUUID());
    when(documents.findById(documentId)).thenReturn(Optional.of(mismatched));
    handler.handle(event(documentId, projectId, ownerId, "text"));

    var ownedByAnotherUser = document(documentId, projectId);
    when(documents.findById(documentId)).thenReturn(Optional.of(ownedByAnotherUser));
    when(projects.findByIdAndOwnerId(projectId, ownerId)).thenReturn(Optional.empty());
    handler.handle(event(documentId, projectId, ownerId, "text"));

    verifyNoInteractions(storage);
    verify(documents, never()).save(any());
  }

  private DocumentContentExtractedEvent event(UUID documentId, UUID projectId, UUID ownerId, String content) {
    return new DocumentContentExtractedEvent(UUID.randomUUID(), "document.parsed", Instant.now(), documentId, projectId, ownerId, content);
  }

  private ProjectDocument document(UUID documentId, UUID projectId) {
    var now = Instant.now();
    return new ProjectDocument(documentId, projectId, "Source", "source.pdf", "application/pdf", 1, "original", now, now, DocumentProcessingStatus.COMPLETED);
  }
}
