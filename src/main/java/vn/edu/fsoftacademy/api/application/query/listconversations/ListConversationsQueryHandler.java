package vn.edu.fsoftacademy.api.application.query.listconversations;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;
public class ListConversationsQueryHandler {
 private final ProjectRepository projects; private final ChatConversationRepository conversations;
 public ListConversationsQueryHandler(ProjectRepository projects, ChatConversationRepository conversations) { this.projects=projects; this.conversations=conversations; }
 public PageResult<ChatConversation> handle(UUID ownerId, UUID projectId, ListConversationsQuery query) { projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new); return conversations.findPageByProjectId(projectId, query); }
}
