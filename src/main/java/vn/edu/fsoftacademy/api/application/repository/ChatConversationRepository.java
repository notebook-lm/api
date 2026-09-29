package vn.edu.fsoftacademy.api.application.repository;

import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.query.listconversations.ListConversationsQuery;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

public interface ChatConversationRepository {
  ChatConversation save(ChatConversation conversation);
  Optional<ChatConversation> findByIdAndProjectId(UUID id, UUID projectId);
  PageResult<ChatConversation> findPageByProjectId(UUID projectId, ListConversationsQuery query);
  void delete(ChatConversation conversation);
}
