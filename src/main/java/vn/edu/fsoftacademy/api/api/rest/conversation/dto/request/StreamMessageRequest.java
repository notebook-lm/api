package vn.edu.fsoftacademy.api.api.rest.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StreamMessageRequest(
        @NotBlank(message = "Message content is required.") @Size(max = 50000, message = "Message content must be at most 50000 characters.") String content) {
}
