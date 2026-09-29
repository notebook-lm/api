package vn.edu.fsoftacademy.api.api.kafka.dto;

import java.util.UUID;

public record DocumentFailureKafkaEvent(UUID eventId, String eventType, String occurredAt, Data data) {
  public record Data(UUID documentId, UUID projectId, UUID userId, String errorCode, String errorMessage) {}
}
