package vn.edu.fsoftacademy.api.api.rest.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fsoftacademy.api.api.rest.document.dto.request.UpdateDocumentRequest;
import vn.edu.fsoftacademy.api.api.rest.document.dto.response.ProjectDocumentPageResponse;
import vn.edu.fsoftacademy.api.api.rest.document.dto.response.ProjectDocumentResponse;
import vn.edu.fsoftacademy.api.api.rest.shared.util.FileUploadUtils;
import vn.edu.fsoftacademy.api.application.command.deletedocument.DeleteDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updatedocument.*;
import vn.edu.fsoftacademy.api.application.command.uploaddocument.*;
import vn.edu.fsoftacademy.api.application.exception.DocumentContentNotAvailableException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.query.getdocument.GetDocumentQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listdocuments.DocumentSortDirection;
import vn.edu.fsoftacademy.api.application.query.listdocuments.DocumentSortField;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQuery;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQueryHandler;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

@RestController
@Tag(name = "Project Documents")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/projects/{projectId}/documents")
public class ProjectDocumentController {
  private final UploadDocumentCommandHandler upload;
  private final ListDocumentsQueryHandler list;
  private final GetDocumentQueryHandler get;
  private final UpdateDocumentCommandHandler update;
  private final DeleteDocumentCommandHandler delete;
  private final ObjectStorage storage;

  public ProjectDocumentController(
      UploadDocumentCommandHandler upload,
      ListDocumentsQueryHandler list,
      GetDocumentQueryHandler get,
      UpdateDocumentCommandHandler update,
      DeleteDocumentCommandHandler delete,
      ObjectStorage storage) {
    this.upload = upload;
    this.list = list;
    this.get = get;
    this.update = update;
    this.delete = delete;
    this.storage = storage;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('document:create')")
  @Operation(summary = "Upload a project document")
  public ProjectDocumentResponse upload(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @RequestPart MultipartFile file,
      @RequestParam(required = false) String title)
      throws Exception {
    if (file.isEmpty())
      throw new IllegalArgumentException("file must not be empty");
    String filename = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
    if (!FileUploadUtils.isSupportedDocumentExtension(filename)) {
      throw new IllegalArgumentException(
          "Unsupported file type. Allowed types: PDF, DOCX, DOC, XLSX, XLS, PPTX, PPT, Markdown, TXT");
    }
    String resolvedTitle = title == null || title.isBlank() ? filename : title.trim();
    if (resolvedTitle.length() > 255)
      throw new IllegalArgumentException("title must be at most 255 characters");
    var r = upload.execute(
        ownerId,
        projectId,
        new UploadDocumentCommand(
            resolvedTitle,
            filename,
            file.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : file.getContentType(),
            file.getSize(),
            file.getInputStream()));
    return response(r);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('document:read')")
  @Operation(summary = "List project documents")
  public ProjectDocumentPageResponse list(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Instant createdFrom,
      @RequestParam(required = false) Instant createdTo,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    if (page < 0 || size < 1 || size > 100)
      throw new IllegalArgumentException(
          "page must be non-negative and size must be between 1 and 100");
    var result =
        list.handle(
            ownerId,
            projectId,
            new ListDocumentsQuery(
                q, createdFrom, createdTo, parseSortBy(sortBy), parseDirection(direction), page, size));
    return new ProjectDocumentPageResponse(
        result.items().stream().map(this::response).toList(),
        result.page(), result.size(), result.totalItems(), result.totalPages(), result.hasNext(), result.hasPrevious());
  }

  @GetMapping("/{documentId}")
  @PreAuthorize("hasAuthority('document:read')")
  @Operation(summary = "Get document metadata")
  public ProjectDocumentResponse get(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @PathVariable UUID documentId) {
    return response(get.handle(ownerId, projectId, documentId));
  }

  @GetMapping("/{documentId}/content")
  @PreAuthorize("hasAuthority('document:read')")
  @Operation(summary = "Download a project document")
  public ResponseEntity<org.springframework.core.io.InputStreamResource> content(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @PathVariable UUID documentId) {
    var d = get.handle(ownerId, projectId, documentId);
    String extractedContentObjectKey = d.getExtractedContentObjectKey();
    if (extractedContentObjectKey == null) {
      throw new DocumentContentNotAvailableException();
    }
    InputStream stream = storage.get(extractedContentObjectKey);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("%s.txt".formatted(d.getTitle())).build().toString())
        .body(new org.springframework.core.io.InputStreamResource(stream));
  }

  @PatchMapping("/{documentId}")
  @PreAuthorize("hasAuthority('document:update')")
  @Operation(summary = "Rename a project document")
  public ProjectDocumentResponse update(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @PathVariable UUID documentId,
      @Valid @RequestBody UpdateDocumentRequest request) {
    return response(
        update.execute(
            ownerId, projectId, documentId, new UpdateDocumentCommand(request.title().trim())));
  }

  @DeleteMapping("/{documentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('document:delete')")
  @Operation(summary = "Delete a project document")
  public void delete(
      @AuthenticationPrincipal UUID ownerId,
      @PathVariable UUID projectId,
      @PathVariable UUID documentId) {
    delete.execute(ownerId, projectId, documentId);
  }

  private DocumentSortField parseSortBy(String value) {
    return switch (value.toLowerCase(Locale.ROOT)) {
      case "createdat" -> DocumentSortField.CREATED_AT;
      case "updatedat" -> DocumentSortField.UPDATED_AT;
      case "title" -> DocumentSortField.TITLE;
      default -> throw new IllegalArgumentException("sortBy must be createdAt, updatedAt, or title");
    };
  }

  private DocumentSortDirection parseDirection(String value) {
    return switch (value.toLowerCase(Locale.ROOT)) {
      case "asc" -> DocumentSortDirection.ASC;
      case "desc" -> DocumentSortDirection.DESC;
      default -> throw new IllegalArgumentException("direction must be asc or desc");
    };
  }

  private ProjectDocumentResponse response(ProjectDocument d) {
    return new ProjectDocumentResponse(
        d.getId(),
        d.getProjectId(),
        d.getTitle(),
        d.getOriginalFilename(),
        d.getContentType(),
        d.getSizeBytes(),
        d.getProcessingStatus().name(),
        d.getCreatedAt(),
        d.getUpdatedAt());
  }

  private ProjectDocumentResponse response(UploadDocumentResult d) {
    return new ProjectDocumentResponse(
        d.id(),
        d.projectId(),
        d.title(),
        d.originalFilename(),
        d.contentType(),
        d.sizeBytes(),
        d.status().name(),
        d.createdAt(),
        d.updatedAt());
  }
}
