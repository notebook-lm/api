package vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;

public class DocumentContentExtractedEventHandler {
  private static final Logger log = LoggerFactory.getLogger(DocumentContentExtractedEventHandler.class);
  private static final String EVENT_TYPE = "document.parsed";
  private static final String CONTENT_TYPE = "text/plain; charset=UTF-8";

  private final ProjectDocumentRepository documents;
  private final ProjectRepository projects;
  private final ObjectStorage storage;
  private final TransactionTemplate transactions;

  public DocumentContentExtractedEventHandler(
      ProjectDocumentRepository documents,
      ProjectRepository projects,
      ObjectStorage storage,
      TransactionTemplate transactions) {
    this.documents = documents;
    this.projects = projects;
    this.storage = storage;
    this.transactions = transactions;
  }

  public void handle(DocumentContentExtractedEvent event) {
    if (!EVENT_TYPE.equals(event.eventType())) {
      log.warn("Ignored extracted content event: eventId={}, documentId={}, contentLength={}, result=unexpected_event_type",
          event.eventId(), event.documentId(), event.content().length());
      return;
    }

    var document = documents.findById(event.documentId()).orElse(null);
    if (document == null) {
      log.warn("Ignored extracted content event: eventId={}, documentId={}, contentLength={}, result=document_not_found",
          event.eventId(), event.documentId(), event.content().length());
      return;
    }
    if (!document.getProjectId().equals(event.projectId())
        || projects.findByIdAndOwnerId(event.projectId(), event.userId()).isEmpty()) {
      log.warn("Ignored extracted content event: eventId={}, documentId={}, contentLength={}, result=ownership_mismatch",
          event.eventId(), event.documentId(), event.content().length());
      return;
    }

    byte[] bytes = event.content().getBytes(StandardCharsets.UTF_8);
    String objectKey = "projects/%s/documents/%s/extracted.txt".formatted(event.projectId(), event.documentId());
    storage.put(objectKey, new ByteArrayInputStream(bytes), bytes.length, CONTENT_TYPE, "extracted.txt");
    transactions.executeWithoutResult(status -> {
      document.storeExtractedContent(objectKey);
      documents.save(document);
    });
    log.info("Processed extracted content event: eventId={}, documentId={}, contentLength={}, result=stored",
        event.eventId(), event.documentId(), event.content().length());
  }
}
