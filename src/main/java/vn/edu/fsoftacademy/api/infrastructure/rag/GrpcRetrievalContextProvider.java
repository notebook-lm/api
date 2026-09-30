package vn.edu.fsoftacademy.api.infrastructure.rag;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import notebooklm.rag.v1.RetrievalServiceGrpc;
import notebooklm.rag.v1.RetrieveContextRequest;
import vn.edu.fsoftacademy.api.application.model.RetrievedContext;
import vn.edu.fsoftacademy.api.application.port.RetrievalContextProvider;

public class GrpcRetrievalContextProvider implements RetrievalContextProvider, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(GrpcRetrievalContextProvider.class);
  private final ManagedChannel channel;
  private final RetrievalServiceGrpc.RetrievalServiceBlockingStub stub;
  private final RagServiceGrpcProperties properties;

  public GrpcRetrievalContextProvider(ManagedChannel channel, RagServiceGrpcProperties properties) {
    this.channel = channel;
    this.stub = RetrievalServiceGrpc.newBlockingStub(channel);
    this.properties = properties;
  }

  @Override
  public RetrievedContext retrieve(String query, UUID projectId, List<UUID> documentIds) {
    if (documentIds.isEmpty()) return RetrievedContext.empty();
    try {
      var request = RetrieveContextRequest.newBuilder()
          .setQuery(query)
          .setProjectId(projectId.toString())
          .addAllDocumentIds(documentIds.stream().map(UUID::toString).toList())
          .setLimit(properties.limit())
          .build();
      var response = stub.withDeadlineAfter(properties.timeoutSeconds(), TimeUnit.SECONDS).retrieveContext(request);
      return new RetrievedContext(response.getContext(), response.getChunksList().stream()
          .map(chunk -> new RetrievedContext.Source(chunk.getFilename(), chunk.getDocumentId(), chunk.getChunkIndex(), chunk.getContent()))
          .toList());
    } catch (StatusRuntimeException exception) {
      log.warn("RAG retrieval failed: status={}, projectId={}, documentCount={}",
          exception.getStatus().getCode(), projectId, documentIds.size());
      return RetrievedContext.empty();
    } catch (RuntimeException exception) {
      log.warn("RAG retrieval failed: projectId={}, documentCount={}", projectId, documentIds.size(), exception);
      return RetrievedContext.empty();
    }
  }

  @Override
  public void close() {
    channel.shutdown();
  }
}
