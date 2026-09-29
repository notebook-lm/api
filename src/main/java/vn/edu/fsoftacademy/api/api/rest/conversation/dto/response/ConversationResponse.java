package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;

public record ConversationResponse(
    UUID id, UUID projectId, String title, Instant lastMessageAt, Instant createdAt, Instant updatedAt) {
  public static ConversationResponse from(ChatConversation conversation) {
    return new ConversationResponse(
        conversation.getId(), conversation.getProjectId(), conversation.getTitle(),
        conversation.getLastMessageAt(), conversation.getCreatedAt(), conversation.getUpdatedAt());
  }
}
