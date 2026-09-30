package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="chat_conversations")
public class ChatConversationJpaEntity {
 @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId; @Column(nullable=false) private String title;
 @Column(name="last_message_at") private Instant lastMessageAt; @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ChatConversationJpaEntity() {}
 public ChatConversationJpaEntity(UUID id, UUID projectId, String title, Instant lastMessageAt, Instant createdAt, Instant updatedAt) { this.id=id;this.projectId=projectId;this.title=title;this.lastMessageAt=lastMessageAt;this.createdAt=createdAt;this.updatedAt=updatedAt; }
 public UUID getId(){return id;} public UUID getProjectId(){return projectId;} public String getTitle(){return title;} public Instant getLastMessageAt(){return lastMessageAt;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
