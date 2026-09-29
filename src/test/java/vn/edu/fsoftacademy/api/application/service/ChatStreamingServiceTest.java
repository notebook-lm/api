package vn.edu.fsoftacademy.api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.model.RetrievedContext;
import vn.edu.fsoftacademy.api.application.port.AiChatProvider;
import vn.edu.fsoftacademy.api.application.port.RetrievalContextProvider;
import vn.edu.fsoftacademy.api.application.query.getconversation.GetConversationQueryHandler;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.*;

class ChatStreamingServiceTest {
  @Test
  void retrievesCompletedProjectDocumentsAndInjectsContextIntoProvider() {
    UUID ownerId = UUID.randomUUID(); UUID projectId = UUID.randomUUID(); UUID conversationId = UUID.randomUUID();
    UUID completedId = UUID.randomUUID();
    var conversations = mock(GetConversationQueryHandler.class);
    var conversationStore = mock(ChatConversationRepository.class);
    var messages = mock(ChatMessageRepository.class);
    var documents = mock(ProjectDocumentRepository.class);
    var provider = mock(AiChatProvider.class);
    var retrieval = mock(RetrievalContextProvider.class);
    var conversation = new ChatConversation(conversationId, projectId, "New conversation", null, Instant.now(), Instant.now());
    var completed = document(completedId, projectId, DocumentProcessingStatus.COMPLETED);
    var pending = document(UUID.randomUUID(), projectId, DocumentProcessingStatus.PENDING);
    when(provider.name()).thenReturn("gemini");
    when(conversations.handle(ownerId, projectId, conversationId)).thenReturn(conversation);
    when(messages.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(messages.findAllByConversationId(conversationId)).thenReturn(List.of());
    when(documents.findAllByProjectId(projectId)).thenReturn(List.of(completed, pending));
    when(retrieval.retrieve("What is covered?", projectId, List.of(completedId)))
        .thenReturn(new RetrievedContext("Coverage is included.", List.of(new RetrievedContext.Source("policy.pdf", completedId.toString(), 4))));
    doAnswer(invocation -> { ((java.util.function.Consumer<String>) invocation.getArgument(2)).accept("Answer"); return null; })
        .when(provider).stream(anyString(), anyList(), any(), any());

    var service = new ChatStreamingService(conversations, conversationStore, messages, documents, provider, retrieval);
    var assistant = service.start(ownerId, projectId, conversationId, "What is covered?");
    var result = service.generate(conversationId, assistant, ignored -> {});

    assertEquals(ChatMessageStatus.COMPLETED, result.getStatus());
    assertEquals("Answer", result.getContent());
    verify(retrieval).retrieve("What is covered?", projectId, List.of(completedId));
    verify(provider).stream(contains("Coverage is included."), anyList(), any(), any());
    verify(provider).stream(contains("policy.pdf — chunk 4"), anyList(), any(), any());
  }

  @Test
  void skipsRetrievalWithoutCompletedDocuments() {
    UUID ownerId = UUID.randomUUID(); UUID projectId = UUID.randomUUID(); UUID conversationId = UUID.randomUUID();
    var conversations = mock(GetConversationQueryHandler.class); var store = mock(ChatConversationRepository.class);
    var messages = mock(ChatMessageRepository.class); var documents = mock(ProjectDocumentRepository.class);
    var provider = mock(AiChatProvider.class); var retrieval = mock(RetrievalContextProvider.class);
    when(provider.name()).thenReturn("gemini");
    when(conversations.handle(eq(ownerId), eq(projectId), eq(conversationId))).thenReturn(new ChatConversation(conversationId, projectId, "New conversation", null, Instant.now(), Instant.now()));
    when(messages.save(any())).thenAnswer(i -> i.getArgument(0)); when(messages.findAllByConversationId(conversationId)).thenReturn(List.of());
    when(documents.findAllByProjectId(projectId)).thenReturn(List.of(document(UUID.randomUUID(), projectId, DocumentProcessingStatus.PROCESSING)));
    doAnswer(i -> null).when(provider).stream(anyString(), anyList(), any(), any());
    var service = new ChatStreamingService(conversations, store, messages, documents, provider, retrieval);
    var assistant = service.start(ownerId, projectId, conversationId, "Hello");
    service.generate(conversationId, assistant, ignored -> {});
    verifyNoInteractions(retrieval);
    verify(provider).stream(eq(""), anyList(), any(), any());
  }

  @Test
  void fallsBackToPlainChatWhenRetrievalIsUnavailable() {
    UUID ownerId = UUID.randomUUID(); UUID projectId = UUID.randomUUID(); UUID conversationId = UUID.randomUUID(); UUID documentId = UUID.randomUUID();
    var conversations = mock(GetConversationQueryHandler.class); var store = mock(ChatConversationRepository.class);
    var messages = mock(ChatMessageRepository.class); var documents = mock(ProjectDocumentRepository.class);
    var provider = mock(AiChatProvider.class); var retrieval = mock(RetrievalContextProvider.class);
    when(provider.name()).thenReturn("gemini");
    when(conversations.handle(eq(ownerId), eq(projectId), eq(conversationId))).thenReturn(new ChatConversation(conversationId, projectId, "New conversation", null, Instant.now(), Instant.now()));
    when(messages.save(any())).thenAnswer(i -> i.getArgument(0)); when(messages.findAllByConversationId(conversationId)).thenReturn(List.of());
    when(documents.findAllByProjectId(projectId)).thenReturn(List.of(document(documentId, projectId, DocumentProcessingStatus.COMPLETED)));
    when(retrieval.retrieve(anyString(), eq(projectId), eq(List.of(documentId)))).thenReturn(RetrievedContext.empty());
    doAnswer(i -> { ((java.util.function.Consumer<String>) i.getArgument(2)).accept("fallback"); return null; }).when(provider).stream(anyString(), anyList(), any(), any());
    var service = new ChatStreamingService(conversations, store, messages, documents, provider, retrieval);
    var assistant = service.start(ownerId, projectId, conversationId, "Hello");
    assertEquals("fallback", service.generate(conversationId, assistant, ignored -> {}).getContent());
    verify(provider).stream(eq(""), anyList(), any(), any());
  }

  private ProjectDocument document(UUID id, UUID projectId, DocumentProcessingStatus status) {
    return new ProjectDocument(id, projectId, "Document", "document.pdf", "application/pdf", 1, "key", Instant.now(), Instant.now(), status);
  }
}
