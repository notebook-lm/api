package vn.edu.fsoftacademy.api.application.query.getconversation;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ConversationNotFoundException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;

public class GetConversationQueryHandler {
    private final ProjectRepository projects;
    private final ChatConversationRepository conversations;

    public GetConversationQueryHandler(ProjectRepository projects, ChatConversationRepository conversations) {
        this.projects = projects;
        this.conversations = conversations;
    }

    public ChatConversation handle(UUID ownerId, UUID projectId, UUID conversationId) {
        projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
        return conversations.findByIdAndProjectId(conversationId, projectId)
                .orElseThrow(ConversationNotFoundException::new);
    }
}
