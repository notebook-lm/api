package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;
import java.time.Instant;
import java.util.UUID;
public record ChatMessageResponse(UUID id,UUID conversationId,String role,String content,String status,String provider,Instant createdAt,Instant updatedAt) {}
