package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;

import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageCitation;

public record CitationSourceResponse(
    int citationNumber, UUID documentId, String filename, int chunkIndex, String excerpt) {
  public static CitationSourceResponse from(ChatMessageCitation citation) {
    return new CitationSourceResponse(
        citation.citationNumber(), citation.documentId(), citation.filename(), citation.chunkIndex(), citation.excerpt());
  }
}
