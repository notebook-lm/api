package vn.edu.fsoftacademy.api.api.rest.conversation.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record UpdateConversationRequest(@NotBlank(message="Conversation title is required.") @Size(max=255, message="Conversation title must be at most 255 characters.") String title) {}
