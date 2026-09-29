package vn.edu.fsoftacademy.api.application.eventhandler.documentprocessing;

import java.util.UUID;

public record DocumentProcessingEvent(
    UUID eventId, String eventType, UUID userId, UUID projectId, UUID documentId) {}
