package vn.edu.fsoftacademy.api.application.command.deleteconversation;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ConversationNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
public class DeleteConversationCommandHandler {
 private final ProjectRepository projects; private final ChatConversationRepository conversations;
 public DeleteConversationCommandHandler(ProjectRepository projects, ChatConversationRepository conversations) { this.projects=projects; this.conversations=conversations; }
 public void execute(UUID ownerId, UUID projectId, UUID conversationId) {
  projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
  conversations.delete(conversations.findByIdAndProjectId(conversationId, projectId).orElseThrow(ConversationNotFoundException::new));
 }
}
