package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.OutboxEventJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.OutboxEventJpaRepository;

class JpaOutboxEventAdapterTest {
  @Test
  void savesDomainEventAndClaimsPendingEventsWithLease() {
    var repository = mock(OutboxEventJpaRepository.class);
    var adapter = new JpaOutboxEventAdapter(repository);
    var now = Instant.parse("2026-01-02T03:04:05Z");
    var lease = now.plusSeconds(30);
    var event = new OutboxEvent(UUID.randomUUID(), "document.uploaded", "document.uploaded", "{}", 0, now, now, null, null);
    var entity = new OutboxEventJpaEntity(event.getId(), event.getEventType(), event.getTopic(), event.getPayload(), 0, now, now, null, null);
    when(repository.findClaimable(any(), any(Pageable.class))).thenReturn(List.of(entity));

    adapter.save(event);
    var claimed = adapter.claimPending(10, now, lease);

    verify(repository).save(argThat(saved -> saved.getId().equals(event.getId()) && saved.getPayload().equals("{}")));
    verify(repository).findClaimable(org.mockito.ArgumentMatchers.eq(now), argThat(page -> page.getPageSize() == 10));
    assertEquals(List.of(event.getId()), claimed.stream().map(OutboxEvent::getId).toList());
  }

  @Test
  void updatesFoundEntitiesForPublishAndFailure() {
    var repository = mock(OutboxEventJpaRepository.class);
    var adapter = new JpaOutboxEventAdapter(repository);
    var now = Instant.parse("2026-01-02T03:04:05Z");
    var event = new OutboxEvent(UUID.randomUUID(), "document.uploaded", "document.uploaded", "{}", 0, now, now, null, null);
    var entity = new OutboxEventJpaEntity(event.getId(), event.getEventType(), event.getTopic(), event.getPayload(), 0, now, now, null, null);
    when(repository.findById(event.getId())).thenReturn(Optional.of(entity));

    adapter.markPublished(event, now);
    adapter.markFailed(event, now.plusSeconds(5), "Kafka down");

    assertEquals(now, entity.getPublishedAt());
    assertEquals(1, entity.getAttempts());
    assertEquals(now.plusSeconds(5), entity.getNextAttemptAt());
    assertEquals("Kafka down", entity.getLastError());
  }
}
