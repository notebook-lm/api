package vn.edu.fsoftacademy.api.api.rest.conversation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.edu.fsoftacademy.api.api.rest.conversation.dto.request.*;
import vn.edu.fsoftacademy.api.api.rest.conversation.dto.response.*;
import vn.edu.fsoftacademy.api.application.command.createconversation.*;
import vn.edu.fsoftacademy.api.application.command.deleteconversation.DeleteConversationCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateconversation.*;
import vn.edu.fsoftacademy.api.application.query.getconversation.GetConversationQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listchatmessages.ListChatMessagesQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listconversations.*;
import vn.edu.fsoftacademy.api.application.service.ChatStreamingService;
import vn.edu.fsoftacademy.api.domain.entity.*;

@RestController
@Tag(name = "Conversations", description = "Private project conversations and AI streaming.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/projects/{projectId}/conversations")
public class ConversationController {
    private final CreateConversationCommandHandler create;
    private final ListConversationsQueryHandler list;
    private final GetConversationQueryHandler get;
    private final UpdateConversationCommandHandler update;
    private final DeleteConversationCommandHandler delete;
    private final ListChatMessagesQueryHandler messages;
    private final ChatStreamingService stream;

    public ConversationController(CreateConversationCommandHandler create, ListConversationsQueryHandler list,
            GetConversationQueryHandler get, UpdateConversationCommandHandler update,
            DeleteConversationCommandHandler delete, ListChatMessagesQueryHandler messages,
            ChatStreamingService stream) {
        this.create = create;
        this.list = list;
        this.get = get;
        this.update = update;
        this.delete = delete;
        this.messages = messages;
        this.stream = stream;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('conversation:create')")
    @Operation(summary = "Create a conversation")
    public ConversationResponse create(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @Valid @RequestBody(required = false) CreateConversationRequest request) {
        return conversation(create.execute(ownerId, projectId,
                new CreateConversationCommand(request == null ? null : request.title())));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('conversation:read')")
    @Operation(summary = "List project conversations")
    public ConversationPageResponse list(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        validatePage(page, size);
        var r = list.handle(ownerId, projectId, new ListConversationsQuery(q, page, size));
        return new ConversationPageResponse(r.items().stream().map(this::conversation).toList(), r.page(), r.size(),
                r.totalItems(), r.totalPages(), r.hasNext(), r.hasPrevious());
    }

    @GetMapping("/{conversationId}")
    @PreAuthorize("hasAuthority('conversation:read')")
    @Operation(summary = "Get a conversation")
    public ConversationResponse get(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId) {
        return conversation(get.handle(ownerId, projectId, conversationId));
    }

    @PatchMapping("/{conversationId}")
    @PreAuthorize("hasAuthority('conversation:update')")
    @Operation(summary = "Rename a conversation")
    public ConversationResponse update(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId, @Valid @RequestBody UpdateConversationRequest request) {
        return conversation(
                update.execute(ownerId, projectId, conversationId, new UpdateConversationCommand(request.title())));
    }

    @DeleteMapping("/{conversationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('conversation:delete')")
    @Operation(summary = "Delete a conversation")
    public void delete(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId) {
        delete.execute(ownerId, projectId, conversationId);
    }

    @GetMapping("/{conversationId}/messages")
    @PreAuthorize("hasAuthority('conversation:read')")
    @Operation(summary = "List conversation messages")
    public ChatMessagePageResponse messages(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        validatePage(page, size);
        var r = messages.handle(ownerId, projectId, conversationId, page, size);
        return new ChatMessagePageResponse(r.items().stream().map(this::message).toList(), r.page(), r.size(),
                r.totalItems(), r.totalPages(), r.hasNext(), r.hasPrevious());
    }

    @PostMapping(value = "/{conversationId}/messages:stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('conversation:message:create')")
    @Operation(summary = "Send a message and stream the AI response")
    public ResponseEntity<SseEmitter> stream(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId, @Valid @RequestBody StreamMessageRequest request) {
        SseEmitter emitter = new SseEmitter(0L);
        ChatMessage assistant = stream.start(ownerId, projectId, conversationId, request.content().strip());
        Runnable cancel = () -> stream.cancel(ownerId, projectId, conversationId, assistant.getId());
        emitter.onCompletion(cancel);
        emitter.onTimeout(cancel);
        CompletableFuture.runAsync(() -> {
            try {
                send(emitter, "started", message(assistant));
                ChatMessage result = stream.generate(conversationId, assistant,
                        delta -> send(emitter, "message", Map.of("delta", delta)));
                if (result.getStatus() == ChatMessageStatus.COMPLETED)
                    send(emitter, "done", message(result));
                emitter.complete();
            } catch (Exception ex) {
                cancel.run();
                try {
                    send(emitter, "error", Map.of("message", safeMessage(ex)));
                } catch (Exception ignored) {
                }
                emitter.complete();
            }
        });
        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "no-cache, no-transform")
                .header("X-Accel-Buffering", "no").body(emitter);
    }

    @PostMapping("/{conversationId}/messages/{messageId}:cancel")
    @PreAuthorize("hasAuthority('conversation:message:create')")
    @Operation(summary = "Cancel an active AI response")
    public ChatMessageResponse cancel(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
            @PathVariable UUID conversationId, @PathVariable UUID messageId) {
        return message(stream.cancel(ownerId, projectId, conversationId, messageId));
    }

    private void send(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event().name(event).data(data, MediaType.APPLICATION_JSON));
        } catch (IOException ex) {
            throw new IllegalStateException("Client disconnected", ex);
        }
    }

    private String safeMessage(Exception ex) {
        return ex instanceof vn.edu.fsoftacademy.api.application.exception.AiProviderException ? ex.getMessage()
                : "Chat generation failed.";
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("page must be non-negative and size must be between 1 and 100");
    }

    private ConversationResponse conversation(ChatConversation c) {
        return new ConversationResponse(c.getId(), c.getProjectId(), c.getTitle(), c.getLastMessageAt(),
                c.getCreatedAt(), c.getUpdatedAt());
    }

    private ChatMessageResponse message(ChatMessage m) {
        return new ChatMessageResponse(m.getId(), m.getConversationId(), m.getRole().name(), m.getContent(),
                m.getStatus().name(), m.getProvider(), m.getCreatedAt(), m.getUpdatedAt());
    }
}
