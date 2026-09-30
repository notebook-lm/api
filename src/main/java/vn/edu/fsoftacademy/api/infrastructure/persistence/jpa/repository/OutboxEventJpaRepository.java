package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.repository.query.Param;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.OutboxEventJpaEntity;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = """
      SELECT event
      FROM OutboxEventJpaEntity event
      WHERE event.publishedAt IS NULL
        AND event.nextAttemptAt <= :now
        AND (event.claimedUntil IS NULL OR event.claimedUntil <= :now)
      ORDER BY event.createdAt ASC
      """)
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
  List<OutboxEventJpaEntity> findClaimable(@Param("now") Instant now, Pageable pageable);
}
