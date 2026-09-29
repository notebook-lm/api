package vn.edu.fsoftacademy.api.application.eventhandler.documentcontentextracted;

import java.time.Instant;
import java.util.UUID;

public record DocumentContentExtractedEvent(
    UUID eventId,
    String eventType,
    Instant occurredAt,
    UUID documentId,
    UUID projectId,
    UUID userId,
    String content) {}
