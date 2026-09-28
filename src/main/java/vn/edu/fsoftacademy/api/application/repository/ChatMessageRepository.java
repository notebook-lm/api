package vn.edu.fsoftacademy.api.application.repository;

import java.util.List;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

public interface ChatMessageRepository {
  ChatMessage save(ChatMessage message);

  List<ChatMessage> findAllByConversationId(UUID conversationId);

  PageResult<ChatMessage> findPageByConversationId(UUID conversationId, int page, int size);
}
