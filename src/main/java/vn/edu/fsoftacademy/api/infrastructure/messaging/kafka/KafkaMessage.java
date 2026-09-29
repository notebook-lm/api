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

  public KafkaMessage(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(String topic, OutboxEvent event) {
    try {
      kafkaTemplate.send(topic, event.getId().toString(), event.getPayload()).get();
      log.info("Published outbox event {} to Kafka topic {}", event.getId(), topic);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while publishing outbox event", ex);
    } catch (Exception ex) {
      throw new IllegalStateException("Could not publish outbox event", ex);
    }
  }
}
