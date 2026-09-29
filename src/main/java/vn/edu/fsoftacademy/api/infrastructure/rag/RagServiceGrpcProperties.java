package vn.edu.fsoftacademy.api.infrastructure.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration for the RAG service gRPC client only. */
@ConfigurationProperties(prefix = "app.grpc.rag-service")
public record RagServiceGrpcProperties(String address, int timeoutSeconds, int limit) {
  public RagServiceGrpcProperties {
    address = address == null || address.isBlank() ? "localhost:50051" : address.strip();
    timeoutSeconds = timeoutSeconds <= 0 ? 5 : timeoutSeconds;
    if (limit < 1 || limit > 20) {
      throw new IllegalArgumentException("app.grpc.rag-service.limit must be between 1 and 20");
    }
  }
}
