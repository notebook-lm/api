package vn.edu.fsoftacademy.api.application.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;

/** Mutable state for an assistant response that is currently being generated. */
public final class ActiveGeneration {
  private final ChatConversation conversation;
  private final ChatMessage assistant;
  private final String query;
  private final UUID projectId;
  private final AtomicBoolean cancelled = new AtomicBoolean();

  public ActiveGeneration(ChatConversation conversation, ChatMessage assistant, String query, UUID projectId) {
    this.conversation = conversation;
    this.assistant = assistant;
    this.query = query;
    this.projectId = projectId;
  }

  public ChatConversation conversation() { return conversation; }
  public ChatMessage assistant() { return assistant; }
  public String query() { return query; }
  public UUID projectId() { return projectId; }
  public boolean isCancelled() { return cancelled.get(); }

  public void cancel() {
    cancelled.set(true);
    assistant.cancel();
  }
}
