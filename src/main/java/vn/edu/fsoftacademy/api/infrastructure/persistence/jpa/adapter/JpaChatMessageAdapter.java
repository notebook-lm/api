package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageCitation;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatMessageCitationJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatMessageJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ChatMessageCitationJpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ChatMessageJpaRepository;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

@Repository
public class JpaChatMessageAdapter implements ChatMessageRepository {
  private final ChatMessageJpaRepository messages;
  private final ChatMessageCitationJpaRepository citations;

  public JpaChatMessageAdapter(ChatMessageJpaRepository messages, ChatMessageCitationJpaRepository citations) {
    this.messages = messages;
    this.citations = citations;
  }

  public ChatMessage save(ChatMessage message) {
    messages.save(toEntity(message));
    citations.deleteByMessageId(message.getId());
    citations.saveAll(message.getCitations().stream().map(citation -> toEntity(message.getId(), citation)).toList());
    return message;
  }

  public Optional<ChatMessage> findById(UUID messageId) {
    return messages.findById(messageId).map(entity -> toDomain(entity, citationsFor(List.of(messageId)).getOrDefault(messageId, List.of())));
  }

  public List<ChatMessage> findAllByConversationId(UUID id) {
    var entities = messages.findAllByConversationIdOrderByCreatedAtAscIdAsc(id);
    var sourceMap = citationsFor(entities.stream().map(ChatMessageJpaEntity::getId).toList());
    return entities.stream().map(entity -> toDomain(entity, sourceMap.getOrDefault(entity.getId(), List.of()))).toList();
  }

  public PageResult<ChatMessage> findPageByConversationId(UUID id, int page, int size) {
    var result = messages.findByConversationId(id, PageRequest.of(page, size, Sort.by("createdAt").ascending().and(Sort.by("id").ascending())));
    var sourceMap = citationsFor(result.getContent().stream().map(ChatMessageJpaEntity::getId).toList());
    return new PageResult<>(result.getContent().stream().map(entity -> toDomain(entity, sourceMap.getOrDefault(entity.getId(), List.of()))).toList(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.hasNext(), result.hasPrevious());
  }

  private Map<UUID, List<ChatMessageCitation>> citationsFor(List<UUID> messageIds) {
    if (messageIds.isEmpty()) return Map.of();
    return citations.findAllByMessageIdInOrderByMessageIdAscCitationNumberAsc(messageIds).stream()
        .collect(Collectors.groupingBy(ChatMessageCitationJpaEntity::getMessageId, LinkedHashMap::new,
            Collectors.mapping(this::toDomain, Collectors.toList())));
  }

  private ChatMessage toDomain(ChatMessageJpaEntity entity, List<ChatMessageCitation> messageCitations) {
    return new ChatMessage(entity.getId(), entity.getConversationId(), entity.getRole(), entity.getContent(), entity.getStatus(), entity.getProvider(), entity.getCreatedAt(), entity.getUpdatedAt(), messageCitations);
  }

  private ChatMessageCitation toDomain(ChatMessageCitationJpaEntity entity) {
    return new ChatMessageCitation(entity.getCitationNumber(), entity.getDocumentId(), entity.getFilename(), entity.getChunkIndex(), entity.getExcerpt());
  }

  private ChatMessageJpaEntity toEntity(ChatMessage message) {
    return new ChatMessageJpaEntity(message.getId(), message.getConversationId(), message.getRole(), message.getContent(), message.getStatus(), message.getProvider(), message.getCreatedAt(), message.getUpdatedAt());
  }

  private ChatMessageCitationJpaEntity toEntity(UUID messageId, ChatMessageCitation citation) {
    return new ChatMessageCitationJpaEntity(messageId, citation.citationNumber(), citation.documentId(), citation.filename(), citation.chunkIndex(), citation.excerpt());
  }
}
