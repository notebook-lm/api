package vn.edu.fsoftacademy.api.application.service;

import java.util.UUID;
import java.util.function.Consumer;
import vn.edu.fsoftacademy.api.application.port.AiChatProvider;
import vn.edu.fsoftacademy.api.application.query.getconversation.GetConversationQueryHandler;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.domain.entity.*;

public class ChatStreamingService {
    private final GetConversationQueryHandler conversations;
    private final ChatConversationRepository conversationStore;
    private final ChatMessageRepository messages;
    private final AiChatProvider provider;

    public ChatStreamingService(GetConversationQueryHandler conversations, ChatConversationRepository conversationStore,
            ChatMessageRepository messages, AiChatProvider provider) {
        this.conversations = conversations;
        this.conversationStore = conversationStore;
        this.messages = messages;
        this.provider = provider;
    }

    public ChatMessage stream(UUID ownerId, UUID projectId, UUID conversationId, String content,
            Consumer<String> onDelta) {
        ChatConversation conversation = conversations.handle(ownerId, projectId, conversationId);
        ChatMessage user = messages.save(
                new ChatMessage(conversationId, ChatMessageRole.USER, content, ChatMessageStatus.COMPLETED, null));
        conversation.touch(user.getCreatedAt());
        if ("New conversation".equals(conversation.getTitle()))
            conversation.rename(titleFrom(content));
        conversationStore.save(conversation);
        ChatMessage assistant = messages.save(new ChatMessage(conversationId, ChatMessageRole.ASSISTANT, "",
                ChatMessageStatus.STREAMING, provider.name()));
        try {
            provider.stream(messages.findAllByConversationId(conversationId), delta -> {
                assistant.append(delta);
                onDelta.accept(delta);
            });
            assistant.complete();
            conversation.touch(assistant.getUpdatedAt());
            conversationStore.save(conversation);
            return messages.save(assistant);
        } catch (RuntimeException e) {
            assistant.fail();
            messages.save(assistant);
            throw e;
        }
    }

    private String titleFrom(String content) {
        String normalized = content.strip().replaceAll("\\s+", " ");
        return normalized.length() > 80 ? normalized.substring(0, 77) + "..." : normalized;
    }
}
