package vn.edu.fsoftacademy.api.application.command.updatedocumentprocessingstatus;

import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.DocumentProcessingStatus;

public record UpdateDocumentProcessingStatusCommand(
    UUID projectId, UUID documentId, DocumentProcessingStatus status) {}
