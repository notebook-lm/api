package vn.edu.fsoftacademy.api.application.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final ConcurrentHashMap<UUID, ActiveGeneration> active = new ConcurrentHashMap<>();

    public ChatStreamingService(GetConversationQueryHandler conversations, ChatConversationRepository conversationStore,
            ChatMessageRepository messages, AiChatProvider provider) {
        this.conversations = conversations;
        this.conversationStore = conversationStore;
        this.messages = messages;
        this.provider = provider;
    }

    public ChatMessage start(UUID ownerId, UUID projectId, UUID conversationId, String content) {
        ChatConversation conversation = conversations.handle(ownerId, projectId, conversationId);
        ChatMessage user = messages.save(
                new ChatMessage(conversationId, ChatMessageRole.USER, content, ChatMessageStatus.COMPLETED, null));
        conversation.touch(user.getCreatedAt());
        if ("New conversation".equals(conversation.getTitle()))
            conversation.rename(titleFrom(content));
        conversationStore.save(conversation);

        ChatMessage assistant = messages.save(new ChatMessage(conversationId, ChatMessageRole.ASSISTANT, "",
                ChatMessageStatus.STREAMING, provider.name()));
        active.put(assistant.getId(), new ActiveGeneration(conversation, assistant));
        return assistant;
    }

    public ChatMessage generate(UUID conversationId, ChatMessage assistant, Consumer<String> onDelta) {
        ActiveGeneration generation = active.get(assistant.getId());
        if (generation == null)
            return assistant;
        try {
            provider.stream(messages.findAllByConversationId(conversationId), delta -> {
                if (!generation.cancelled.get()) {
                    assistant.append(delta);
                    onDelta.accept(delta);
                }
            }, generation.cancelled::get);
            if (generation.cancelled.get()) {
                assistant.cancel();
            } else {
                assistant.complete();
                generation.conversation.touch(assistant.getUpdatedAt());
                conversationStore.save(generation.conversation);
            }
            return messages.save(assistant);
        } catch (RuntimeException e) {
            if (generation.cancelled.get()) {
                assistant.cancel();
                return messages.save(assistant);
            }
            assistant.fail();
            messages.save(assistant);
            throw e;
        } finally {
            active.remove(assistant.getId(), generation);
        }
    }

    public ChatMessage cancel(UUID ownerId, UUID projectId, UUID conversationId, UUID messageId) {
        conversations.handle(ownerId, projectId, conversationId);
        ChatMessage message = messages.findById(messageId)
                .filter(candidate -> candidate.getConversationId().equals(conversationId)
                        && candidate.getRole() == ChatMessageRole.ASSISTANT)
                .orElseThrow(() -> new IllegalArgumentException("Assistant message not found."));
        ActiveGeneration generation = active.get(messageId);
        if (generation != null) {
            generation.cancelled.set(true);
            generation.assistant.cancel();
            return messages.save(generation.assistant);
        }
        return message;
    }

    private String titleFrom(String content) {
        String normalized = content.strip().replaceAll("\\s+", " ");
        return normalized.length() > 80 ? normalized.substring(0, 77) + "..." : normalized;
    }

    private static final class ActiveGeneration {
        private final ChatConversation conversation;
        private final ChatMessage assistant;
        private final AtomicBoolean cancelled = new AtomicBoolean();

        private ActiveGeneration(ChatConversation conversation, ChatMessage assistant) {
            this.conversation = conversation;
            this.assistant = assistant;
        }
    }
}
