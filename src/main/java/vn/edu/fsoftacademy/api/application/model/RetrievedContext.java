package vn.edu.fsoftacademy.api.application.model;

import java.util.List;

public record RetrievedContext(String context, List<Source> sources) {
  public RetrievedContext {
    context = context == null ? "" : context.strip();
    sources = List.copyOf(sources == null ? List.of() : sources);
  }

  public static RetrievedContext empty() {
    return new RetrievedContext("", List.of());
  }

  public boolean isUsable() {
    return !context.isBlank();
  }

  public record Source(String filename, String documentId, int chunkIndex, String excerpt) {}
}
