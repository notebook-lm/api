package vn.edu.fsoftacademy.api.infrastructure.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

class KafkaMessageTest {
  @Test
  void sendsEventUsingIdAsKafkaKey() {
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> template = mock(KafkaTemplate.class);
    var event = new OutboxEvent(UUID.randomUUID(), "document.uploaded", "document.uploaded", "{\"id\":1}", 0, Instant.now(), Instant.now(), null, null);
    when(template.send("document.uploaded", event.getId().toString(), event.getPayload())).thenReturn(CompletableFuture.completedFuture(null));

    new KafkaMessage(template).publish(event.getTopic(), event);

    verify(template).send("document.uploaded", event.getId().toString(), event.getPayload());
  }

  @Test
  void wrapsDeliveryFailure() {
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> template = mock(KafkaTemplate.class);
    var event = new OutboxEvent(UUID.randomUUID(), "document.uploaded", "document.uploaded", "{}", 0, Instant.now(), Instant.now(), null, null);
    when(template.send("document.uploaded", event.getId().toString(), event.getPayload())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka down")));

    var error = assertThrows(IllegalStateException.class, () -> new KafkaMessage(template).publish(event.getTopic(), event));
    assertEquals("Could not publish outbox event", error.getMessage());
  }

}
