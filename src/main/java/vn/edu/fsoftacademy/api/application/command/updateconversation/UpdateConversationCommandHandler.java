package vn.edu.fsoftacademy.api.application.command.updateconversation;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ConversationNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;
public class UpdateConversationCommandHandler {
 private final ProjectRepository projects; private final ChatConversationRepository conversations;
 public UpdateConversationCommandHandler(ProjectRepository projects, ChatConversationRepository conversations) { this.projects=projects; this.conversations=conversations; }
 public ChatConversation execute(UUID ownerId, UUID projectId, UUID conversationId, UpdateConversationCommand command) {
  projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
  ChatConversation c=conversations.findByIdAndProjectId(conversationId, projectId).orElseThrow(ConversationNotFoundException::new);
  c.rename(command.title().strip()); return conversations.save(c);
 }
}
