package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;

import java.util.List;
import java.util.UUID;

public record StreamSourcesResponse(UUID messageId, List<CitationSourceResponse> sources) {
  public StreamSourcesResponse {
    sources = List.copyOf(sources == null ? List.of() : sources);
  }
}
