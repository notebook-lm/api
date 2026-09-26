package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.OutboxEventJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.OutboxEventJpaRepository;

@Repository
public class JpaOutboxEventAdapter implements OutboxEventRepository {
  private final OutboxEventJpaRepository events;

  public JpaOutboxEventAdapter(OutboxEventJpaRepository events) {
    this.events = events;
  }

  @Transactional
  public OutboxEvent save(OutboxEvent event) {
    events.save(entity(event));
    return event;
  }

  @Transactional
  public List<OutboxEvent> claimPending(int limit, Instant now, Instant claimedUntil) {
    var pending = events.findClaimable(now, PageRequest.of(0, limit));
    pending.forEach(event -> event.claimUntil(claimedUntil));
    return pending.stream().map(this::domain).toList();
  }

  @Transactional
  public void markPublished(OutboxEvent event, Instant publishedAt) {
    events.findById(event.getId()).ifPresent(entity -> entity.markPublished(publishedAt));
  }

  @Transactional
  public void markFailed(OutboxEvent event, Instant nextAttemptAt, String error) {
    events.findById(event.getId()).ifPresent(entity -> entity.markFailed(nextAttemptAt, error));
  }

  private OutboxEvent domain(OutboxEventJpaEntity e) {
    return new OutboxEvent(e.getId(), e.getEventType(), e.getPayload(), e.getAttempts(), e.getNextAttemptAt(),
        e.getCreatedAt(), e.getPublishedAt(), e.getLastError());
  }

  private OutboxEventJpaEntity entity(OutboxEvent e) {
    return new OutboxEventJpaEntity(e.getId(), e.getEventType(), e.getPayload(), e.getAttempts(), e.getNextAttemptAt(),
        e.getCreatedAt(), e.getPublishedAt(), e.getLastError());
  }
}
