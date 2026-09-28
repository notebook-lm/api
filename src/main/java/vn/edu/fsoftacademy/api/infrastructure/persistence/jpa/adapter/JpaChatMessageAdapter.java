package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatMessageJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ChatMessageJpaRepository;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

@Repository
public class JpaChatMessageAdapter implements ChatMessageRepository {
    private final ChatMessageJpaRepository messages;

    public JpaChatMessageAdapter(ChatMessageJpaRepository messages) {
        this.messages = messages;
    }

    public ChatMessage save(ChatMessage m) {
        messages.save(toEntity(m));
        return m;
    }

    public Optional<ChatMessage> findById(UUID messageId) {
        return messages.findById(messageId).map(this::toDomain);
    }

    public List<ChatMessage> findAllByConversationId(UUID id) {
        return messages.findAllByConversationIdOrderByCreatedAtAscIdAsc(id).stream().map(this::toDomain).toList();
    }

    public PageResult<ChatMessage> findPageByConversationId(UUID id, int page, int size) {
        var r = messages.findByConversationId(id,
                PageRequest.of(page, size, Sort.by("createdAt").ascending().and(Sort.by("id").ascending())));
        return new PageResult<>(r.getContent().stream().map(this::toDomain).toList(), r.getNumber(), r.getSize(),
                r.getTotalElements(), r.getTotalPages(), r.hasNext(), r.hasPrevious());
    }

    private ChatMessage toDomain(ChatMessageJpaEntity e) {
        return new ChatMessage(e.getId(), e.getConversationId(), e.getRole(), e.getContent(), e.getStatus(),
                e.getProvider(), e.getCreatedAt(), e.getUpdatedAt());
    }

    private ChatMessageJpaEntity toEntity(ChatMessage e) {
        return new ChatMessageJpaEntity(e.getId(), e.getConversationId(), e.getRole(), e.getContent(), e.getStatus(),
                e.getProvider(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
