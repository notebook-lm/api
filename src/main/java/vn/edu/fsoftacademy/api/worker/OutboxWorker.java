package vn.edu.fsoftacademy.api.worker;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.port.MessagePublisher;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;

@Component
public class OutboxWorker {
  private static final Logger log = LoggerFactory.getLogger(OutboxWorker.class);
  private final OutboxEventRepository events;
  private final MessagePublisher publisher;
  private final OutboxWorkerProperties properties;

  public OutboxWorker(
      OutboxEventRepository events, MessagePublisher publisher, OutboxWorkerProperties properties) {
    this.events = events;
    this.publisher = publisher;
    this.properties = properties;
  }

  @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:1000}")
  public void dispatch() {
    if (!properties.enabled()) {
      return;
    }
    Instant now = Instant.now();
    Instant claimedUntil = now.plusSeconds(properties.claimLeaseSeconds());
    events.claimPending(properties.batchSize(), now, claimedUntil).forEach(event -> {
      try {
        publisher.publish(event.getTopic(), event);
        events.markPublished(event, Instant.now());
      } catch (RuntimeException ex) {
        int nextAttempt = event.getAttempts() + 1;
        String error = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        if (nextAttempt >= properties.maxAttempts()) {
          events.markFailed(event, Instant.parse("9999-12-31T23:59:59Z"), error);
          log.error("Outbox event {} exhausted {} delivery attempts", event.getId(), nextAttempt, ex);
          return;
        }
        long multiplier = 1L << Math.min(nextAttempt - 1, 20);
        long retryDelaySeconds = Math.min(
            properties.initialBackoffSeconds() * multiplier,
            properties.maxBackoffSeconds());
        Instant retryAt = Instant.now().plus(Duration.ofSeconds(retryDelaySeconds));
        events.markFailed(event, retryAt, error);
        log.warn("Outbox event {} delivery attempt {} failed; retrying at {}", event.getId(), nextAttempt, retryAt, ex);
      }
    });
  }
}
