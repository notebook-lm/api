package vn.edu.fsoftacademy.api.api.kafka.dto;

import java.util.UUID;

public record DocumentProcessingKafkaEvent(
    UUID eventId, String eventType, String occurredAt, Data data) {
  public record Data(UUID documentId, UUID projectId, String status) {
  }
}
