package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "chat_message_citations")
@IdClass(ChatMessageCitationJpaEntity.Id.class)
public class ChatMessageCitationJpaEntity {
  @jakarta.persistence.Id
  @Column(name = "message_id", nullable = false)
  private UUID messageId;

  @jakarta.persistence.Id
  @Column(name = "citation_number", nullable = false)
  private int citationNumber;

  @Column(name = "document_id", nullable = false)
  private UUID documentId;

  @Column(nullable = false)
  private String filename;

  @Column(name = "chunk_index", nullable = false)
  private int chunkIndex;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String excerpt;

  protected ChatMessageCitationJpaEntity() {}

  public ChatMessageCitationJpaEntity(
      UUID messageId, int citationNumber, UUID documentId, String filename, int chunkIndex, String excerpt) {
    this.messageId = messageId;
    this.citationNumber = citationNumber;
    this.documentId = documentId;
    this.filename = filename;
    this.chunkIndex = chunkIndex;
    this.excerpt = excerpt;
  }

  public UUID getMessageId() { return messageId; }
  public int getCitationNumber() { return citationNumber; }
  public UUID getDocumentId() { return documentId; }
  public String getFilename() { return filename; }
  public int getChunkIndex() { return chunkIndex; }
  public String getExcerpt() { return excerpt; }

  public static class Id implements Serializable {
    private UUID messageId;
    private int citationNumber;

    public Id() {}

    public Id(UUID messageId, int citationNumber) {
      this.messageId = messageId;
      this.citationNumber = citationNumber;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) return true;
      if (!(other instanceof Id that)) return false;
      return citationNumber == that.citationNumber && messageId.equals(that.messageId);
    }

    @Override
    public int hashCode() {
      return 31 * messageId.hashCode() + citationNumber;
    }
  }
}
