package vn.edu.fsoftacademy.api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.port.AiChatProvider;
import vn.edu.fsoftacademy.api.application.query.getconversation.GetConversationQueryHandler;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.domain.entity.*;

class ChatStreamingServiceTest {
  @Test
  void persistsUserAndCompletedAssistantMessagesAndStreamsDeltas() {
    UUID ownerId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    UUID conversationId = UUID.randomUUID();
    ChatConversation conversation = new ChatConversation(conversationId, projectId, "New conversation", null, Instant.now(), Instant.now());
    var conversations = mock(GetConversationQueryHandler.class);
    var conversationStore = mock(ChatConversationRepository.class);
    var messages = mock(ChatMessageRepository.class);
    var provider = mock(AiChatProvider.class);
    when(provider.name()).thenReturn("gemini");
    when(conversations.handle(ownerId, projectId, conversationId)).thenReturn(conversation);
    when(messages.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(messages.findAllByConversationId(conversationId)).thenReturn(List.of());
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked") var callback = (java.util.function.Consumer<String>) invocation.getArgument(1);
      callback.accept("Hello");
      callback.accept(" world");
      return null;
    }).when(provider).stream(anyList(), any(), any());

    var service = new ChatStreamingService(conversations, conversationStore, messages, provider);
    var assistant = service.start(ownerId, projectId, conversationId, "What is this?");
    var result = service.generate(conversationId, assistant, ignored -> {});

    assertEquals(ChatMessageRole.ASSISTANT, result.getRole());
    assertEquals(ChatMessageStatus.COMPLETED, result.getStatus());
    assertEquals("Hello world", result.getContent());
    assertEquals("What is this?", conversation.getTitle());
    verify(messages, times(3)).save(any(ChatMessage.class));
  }
}
