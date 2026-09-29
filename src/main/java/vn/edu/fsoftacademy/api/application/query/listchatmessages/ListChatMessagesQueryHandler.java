package vn.edu.fsoftacademy.api.application.query.listchatmessages;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.query.getconversation.GetConversationQueryHandler;
import vn.edu.fsoftacademy.api.application.repository.ChatMessageRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;
public class ListChatMessagesQueryHandler {
 private final GetConversationQueryHandler conversations; private final ChatMessageRepository messages;
 public ListChatMessagesQueryHandler(GetConversationQueryHandler conversations, ChatMessageRepository messages) { this.conversations=conversations; this.messages=messages; }
 public PageResult<ChatMessage> handle(UUID ownerId, UUID projectId, UUID conversationId, int page, int size) { conversations.handle(ownerId, projectId, conversationId); return messages.findPageByConversationId(conversationId, page, size); }
}
