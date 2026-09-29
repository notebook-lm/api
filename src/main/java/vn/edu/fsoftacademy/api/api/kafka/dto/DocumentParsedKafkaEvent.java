package vn.edu.fsoftacademy.api.api.kafka.dto;

import java.util.UUID;

public record DocumentParsedKafkaEvent(UUID eventId, String eventType, String occurredAt, Data data) {
  public record Data(UUID documentId, UUID projectId, UUID userId, String content) {}
}
