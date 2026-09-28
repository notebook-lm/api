package vn.edu.fsoftacademy.api.application.command.createconversation;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;

public class CreateConversationCommandHandler {
    private final ProjectRepository projects;
    private final ChatConversationRepository conversations;

    public CreateConversationCommandHandler(ProjectRepository projects, ChatConversationRepository conversations) {
        this.projects = projects;
        this.conversations = conversations;
    }

    public ChatConversation execute(UUID ownerId, UUID projectId, CreateConversationCommand command) {
        projects.findByIdAndOwnerId(projectId, ownerId).orElseThrow(ProjectNotFoundException::new);
        String title = command.title() == null || command.title().isBlank() ? "New conversation"
                : command.title().strip();
        return conversations.save(new ChatConversation(projectId, title));
    }
}
