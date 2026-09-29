package vn.edu.fsoftacademy.api.application.eventhandler.documentparsedfailed;

import java.util.UUID;

public record DocumentParsedFailedEvent(
    UUID eventId, String eventType, UUID userId, UUID projectId, UUID documentId,
    String errorCode, String errorMessage) {}
