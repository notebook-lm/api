package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessed;

import java.util.UUID;

public record DocumentProcessedEvent(
    UUID eventId, String eventType, UUID userId, UUID projectId, UUID documentId, int chunkCount) {}
