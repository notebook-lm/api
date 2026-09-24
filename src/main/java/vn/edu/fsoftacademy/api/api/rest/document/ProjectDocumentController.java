package vn.edu.fsoftacademy.api.api.rest.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fsoftacademy.api.api.rest.document.dto.request.UpdateDocumentRequest;
import vn.edu.fsoftacademy.api.api.rest.document.dto.response.ProjectDocumentResponse;
import vn.edu.fsoftacademy.api.api.rest.shared.util.FileUploadUtils;
import vn.edu.fsoftacademy.api.application.command.deletedocument.DeleteDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updatedocument.*;
import vn.edu.fsoftacademy.api.application.command.uploaddocument.*;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.query.getdocument.GetDocumentQueryHandler;
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
  public java.util.List<ProjectDocumentResponse> list(
      @AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId) {
    return list.handle(ownerId, projectId).stream().map(this::response).toList();
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
    InputStream stream = storage.get(d.getObjectKey());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(d.getContentType()))
        .contentLength(d.getSizeBytes())
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(d.getOriginalFilename()).build().toString())
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

  private ProjectDocumentResponse response(ProjectDocument d) {
    return new ProjectDocumentResponse(
        d.getId(),
        d.getProjectId(),
        d.getTitle(),
        d.getOriginalFilename(),
        d.getContentType(),
        d.getSizeBytes(),
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
        d.createdAt(),
        d.updatedAt());
  }
}
