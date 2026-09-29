package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.*;
@Entity @Table(name="chat_messages")
public class ChatMessageJpaEntity {
 @Id private UUID id; @Column(name="conversation_id",nullable=false) private UUID conversationId; @Enumerated(EnumType.STRING) @Column(nullable=false) private ChatMessageRole role; @Column(nullable=false,columnDefinition="TEXT") private String content; @Enumerated(EnumType.STRING) @Column(nullable=false) private ChatMessageStatus status; private String provider; @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ChatMessageJpaEntity() {}
 public ChatMessageJpaEntity(UUID id,UUID conversationId,ChatMessageRole role,String content,ChatMessageStatus status,String provider,Instant createdAt,Instant updatedAt){this.id=id;this.conversationId=conversationId;this.role=role;this.content=content;this.status=status;this.provider=provider;this.createdAt=createdAt;this.updatedAt=updatedAt;}
 public UUID getId(){return id;} public UUID getConversationId(){return conversationId;} public ChatMessageRole getRole(){return role;} public String getContent(){return content;} public ChatMessageStatus getStatus(){return status;} public String getProvider(){return provider;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
