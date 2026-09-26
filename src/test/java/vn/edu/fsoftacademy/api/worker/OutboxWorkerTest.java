package vn.edu.fsoftacademy.api.worker;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.port.MessagePublisher;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

class OutboxWorkerTest {
  private final OutboxEvent event = new OutboxEvent(
      UUID.randomUUID(), "document.uploaded", "{}", 0, Instant.now(), Instant.now(), null, null);

  @Test
  void marksClaimedEventPublishedAfterDelivery() {
    var events = mock(OutboxEventRepository.class);
    var publisher = mock(MessagePublisher.class);
    when(events.claimPending(eq(20), any(), any())).thenReturn(List.of(event));

    worker(events, publisher).dispatch();

    verify(publisher).publish(event);
    verify(events).markPublished(eq(event), any());
    verify(events, never()).markFailed(any(), any(), any());
  }

  @Test
  void schedulesRetryAfterTransientDeliveryFailure() {
    var events = mock(OutboxEventRepository.class);
    var publisher = mock(MessagePublisher.class);
    when(events.claimPending(eq(20), any(), any())).thenReturn(List.of(event));
    doThrow(new IllegalStateException("Kafka unavailable")).when(publisher).publish(event);

    worker(events, publisher).dispatch();

    verify(events).markFailed(eq(event), any(), eq("Kafka unavailable"));
    verify(events, never()).markPublished(any(), any());
  }

  @Test
  void exhaustsEventAtConfiguredAttemptLimit() {
    var events = mock(OutboxEventRepository.class);
    var publisher = mock(MessagePublisher.class);
    var exhausted = new OutboxEvent(
        UUID.randomUUID(), "document.uploaded", "{}", 1, Instant.now(), Instant.now(), null, null);
    when(events.claimPending(eq(20), any(), any())).thenReturn(List.of(exhausted));
    doThrow(new IllegalStateException("Kafka unavailable")).when(publisher).publish(exhausted);

    worker(events, publisher).dispatch();

    verify(events).markFailed(eq(exhausted), eq(Instant.parse("9999-12-31T23:59:59Z")), eq("Kafka unavailable"));
  }

  private OutboxWorker worker(OutboxEventRepository events, MessagePublisher publisher) {
    return new OutboxWorker(events, publisher, new OutboxWorkerProperties(true, 1000, 20, 2, 5, 60, 30, "document.uploaded"));
  }
}
