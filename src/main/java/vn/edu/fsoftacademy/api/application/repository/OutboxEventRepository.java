package vn.edu.fsoftacademy.api.application.repository;

import java.time.Instant;
import java.util.List;
import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

public interface OutboxEventRepository {
  OutboxEvent save(OutboxEvent event);

  List<OutboxEvent> claimPending(int limit, Instant now, Instant claimedUntil);

  void markPublished(OutboxEvent event, Instant publishedAt);

  void markFailed(OutboxEvent event, Instant nextAttemptAt, String error);
}
