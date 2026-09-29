package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessedfailed;

import java.util.UUID;

public record DocumentProcessedFailedEvent(
    UUID eventId, String eventType, UUID userId, UUID projectId, UUID documentId,
    String errorCode, String errorMessage) {}
