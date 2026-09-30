package vn.edu.fsoftacademy.api.application.port;

import java.util.List;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.model.RetrievedContext;

public interface RetrievalContextProvider {
  RetrievedContext retrieve(String query, UUID projectId, List<UUID> documentIds);
}
