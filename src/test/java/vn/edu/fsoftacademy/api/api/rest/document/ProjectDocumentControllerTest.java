package vn.edu.fsoftacademy.api.api.rest.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import vn.edu.fsoftacademy.api.application.command.deletedocument.DeleteDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updatedocument.*;
import vn.edu.fsoftacademy.api.application.command.uploaddocument.*;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.query.getdocument.GetDocumentQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsResult;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

class ProjectDocumentControllerTest {
  private final UUID ownerId = UUID.randomUUID();
  private final UUID projectId = UUID.randomUUID();
  private final UUID documentId = UUID.randomUUID();
  private UploadDocumentCommandHandler upload;
  private ListDocumentsQueryHandler list;
  private GetDocumentQueryHandler get;
  private UpdateDocumentCommandHandler update;
  private DeleteDocumentCommandHandler delete;
  private ObjectStorage storage;
  private ProjectDocumentController controller;

  @BeforeEach
  void setUp() {
    upload = mock(UploadDocumentCommandHandler.class);
    list = mock(ListDocumentsQueryHandler.class);
    get = mock(GetDocumentQueryHandler.class);
    update = mock(UpdateDocumentCommandHandler.class);
    delete = mock(DeleteDocumentCommandHandler.class);
    storage = mock(ObjectStorage.class);
    controller = new ProjectDocumentController(upload, list, get, update, delete, storage);
  }

  @Test
  void uploadUsesFilenameAsDefaultTitleAndMapsMultipartContent() throws Exception {
    var now = Instant.now();
    when(upload.execute(eq(ownerId), eq(projectId), any()))
        .thenReturn(
            new UploadDocumentResult(
                documentId, projectId, "source.pdf", "source.pdf", "application/pdf", 3, now, now));
    var file = new MockMultipartFile("file", "source.pdf", "application/pdf", new byte[] {1, 2, 3});
    var response = controller.upload(ownerId, projectId, file, null);
    assertEquals(documentId, response.id());
    verify(upload)
        .execute(
            eq(ownerId),
            eq(projectId),
            argThat(
                command ->
                    command.title().equals("source.pdf")
                        && command.sizeBytes() == 3
                        && command.contentType().equals("application/pdf")));
  }

  @Test
  void uploadRejectsUnsupportedFileExtension() {
    var file =
        new MockMultipartFile("file", "malware.exe", "application/octet-stream", new byte[] {1});

    var exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class, () -> controller.upload(ownerId, projectId, file, null));

    assertEquals(
        "Unsupported file type. Allowed types: PDF, DOCX, DOC, XLSX, XLS, PPTX, PPT, Markdown, TXT",
        exception.getMessage());
    verifyNoInteractions(upload);
  }

  @Test
  void listUpdateDeleteUseProjectScopedAuthenticatedCalls() {
    var d = document();
    when(list.handle(eq(ownerId), eq(projectId), any()))
        .thenReturn(
            new ListDocumentsResult(
                List.of(d),
                0, 20, 1, 1, false, false));
    when(update.execute(eq(ownerId), eq(projectId), eq(documentId), any())).thenReturn(d);
    assertEquals(1, controller.list(ownerId, projectId, null, null, null, "createdAt", "desc", 0, 20).items().size());
    assertEquals(
        "Source",
        controller
            .update(
                ownerId,
                projectId,
                documentId,
                new vn.edu.fsoftacademy.api.api.rest.document.dto.request.UpdateDocumentRequest(
                    "Source"))
            .title());
    controller.delete(ownerId, projectId, documentId);
    verify(update).execute(ownerId, projectId, documentId, new UpdateDocumentCommand("Source"));
    verify(delete).execute(ownerId, projectId, documentId);
  }

  @Test
  void downloadStreamsStoredObjectWithOriginalMetadata() {
    var d = document();
    when(get.handle(ownerId, projectId, documentId)).thenReturn(d);
    when(storage.get("object-key")).thenReturn(new ByteArrayInputStream(new byte[] {1}));
    var response = controller.content(ownerId, projectId, documentId);
    assertEquals("application/pdf", response.getHeaders().getContentType().toString());
    assertEquals(5, response.getHeaders().getContentLength());
    verify(storage).get("object-key");
  }

  private ProjectDocument document() {
    var now = Instant.now();
    return new ProjectDocument(
        documentId,
        projectId,
        "Source",
        "source.pdf",
        "application/pdf",
        5,
        "object-key",
        now,
        now);
  }
}
