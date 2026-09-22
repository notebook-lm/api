package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vn.edu.fsoftacademy.api.domain.entity.RefreshSession;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RefreshSessionJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.RefreshSessionJpaRepository;

class JpaRefreshSessionAdapterTest {
  private final RefreshSessionJpaRepository repository = mock(RefreshSessionJpaRepository.class);
  private final JpaRefreshSessionAdapter adapter = new JpaRefreshSessionAdapter(repository);

  @Test
  void findsAndMapsSessionByTokenHash() {
    RefreshSessionJpaEntity entity = entity();
    when(repository.findByTokenHash("hash")).thenReturn(Optional.of(entity));

    var session = adapter.findByTokenHash("hash").orElseThrow();

    assertEquals(entity.getId(), session.getId());
    assertEquals(entity.getUserId(), session.getUserId());
    assertEquals(entity.getExpiresAt(), session.getExpiresAt());
  }

  @Test
  void mapsSessionsForUser() {
    RefreshSessionJpaEntity entity = entity();
    when(repository.findByUserId(entity.getUserId())).thenReturn(List.of(entity));

    var sessions = adapter.findByUserId(entity.getUserId());

    assertEquals(1, sessions.size());
    assertEquals(entity.getTokenHash(), sessions.getFirst().getTokenHash());
  }

  @Test
  void savesMappedEntityAndReturnsOriginalSession() {
    RefreshSession session = domainSession();

    RefreshSession saved = adapter.save(session);

    ArgumentCaptor<RefreshSessionJpaEntity> captor = ArgumentCaptor.forClass(RefreshSessionJpaEntity.class);
    verify(repository).save(captor.capture());
    assertSame(session, saved);
    assertEquals(session.getId(), captor.getValue().getId());
    assertEquals(session.getTokenHash(), captor.getValue().getTokenHash());
  }

  @Test
  void deletesAllSessionsForUser() {
    UUID userId = UUID.randomUUID();
    adapter.deleteAllByUserId(userId);
    verify(repository).deleteAllByUserId(userId);
  }

  private RefreshSessionJpaEntity entity() {
    RefreshSession session = domainSession();
    return new RefreshSessionJpaEntity(session.getId(), session.getUserId(), session.getTokenHash(),
        session.getExpiresAt(), session.getRevokedAt(), session.getCreatedAt());
  }

  private RefreshSession domainSession() {
    Instant now = Instant.now();
    return new RefreshSession(UUID.randomUUID(), UUID.randomUUID(), "hash", now.plusSeconds(3600), null, now);
  }
}
