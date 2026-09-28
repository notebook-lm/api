package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;
import java.time.Instant;
import java.util.UUID;
public record ConversationResponse(UUID id,UUID projectId,String title,Instant lastMessageAt,Instant createdAt,Instant updatedAt) {}
