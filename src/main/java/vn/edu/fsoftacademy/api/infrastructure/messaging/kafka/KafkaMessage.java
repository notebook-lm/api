package vn.edu.fsoftacademy.api.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.port.MessagePublisher;
import vn.edu.fsoftacademy.api.worker.OutboxWorkerProperties;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

@Component
public class KafkaMessage implements MessagePublisher {
  private static final Logger log = LoggerFactory.getLogger(KafkaMessage.class);
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final OutboxWorkerProperties properties;

  public KafkaMessage(KafkaTemplate<String, String> kafkaTemplate, OutboxWorkerProperties properties) {
    this.kafkaTemplate = kafkaTemplate;
    this.properties = properties;
  }

  @Override
  public void publish(OutboxEvent event) {
    try {
      kafkaTemplate.send(properties.topic(), event.getId().toString(), event.getPayload()).get();
      log.info("Published outbox event {} to Kafka topic {}", event.getId(), properties.topic());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while publishing outbox event", ex);
    } catch (Exception ex) {
      throw new IllegalStateException("Could not publish outbox event", ex);
    }
  }
}
