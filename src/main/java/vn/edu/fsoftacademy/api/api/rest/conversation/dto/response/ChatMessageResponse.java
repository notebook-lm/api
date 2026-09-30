package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;

public record ChatMessageResponse(
    UUID id,
    UUID conversationId,
    String role,
    String content,
    String status,
    String provider,
    Instant createdAt,
    Instant updatedAt,
    List<CitationSourceResponse> sources) {
  public ChatMessageResponse {
    sources = List.copyOf(sources == null ? List.of() : sources);
  }

  public static ChatMessageResponse from(ChatMessage chatMessage) {
    return new ChatMessageResponse(
        chatMessage.getId(), chatMessage.getConversationId(), chatMessage.getRole().name(),
        chatMessage.getContent(), chatMessage.getStatus().name(), chatMessage.getProvider(),
        chatMessage.getCreatedAt(), chatMessage.getUpdatedAt(),
        chatMessage.getCitations().stream().map(CitationSourceResponse::from).toList());
  }
}
