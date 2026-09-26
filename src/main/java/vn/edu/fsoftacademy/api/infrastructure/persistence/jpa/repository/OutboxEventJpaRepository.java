package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.OutboxEventJpaEntity;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
  @Query("""
      SELECT event
      FROM OutboxEventJpaEntity event
      WHERE event.publishedAt IS NULL
        AND event.nextAttemptAt <= :now
      ORDER BY event.createdAt ASC
      """)
  List<OutboxEventJpaEntity> findPending(Instant now);
}
