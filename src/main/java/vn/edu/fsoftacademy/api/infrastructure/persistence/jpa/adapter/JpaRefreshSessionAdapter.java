package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.RefreshSessionRepository;
import vn.edu.fsoftacademy.api.domain.entity.RefreshSession;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RefreshSessionJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.RefreshSessionJpaRepository;

@Repository
public class JpaRefreshSessionAdapter implements RefreshSessionRepository {
  private final RefreshSessionJpaRepository repository;

  public JpaRefreshSessionAdapter(RefreshSessionJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<RefreshSession> findByTokenHash(String tokenHash) {
    return repository.findByTokenHash(tokenHash).map(this::toDomain);
  }

  @Override
  public List<RefreshSession> findByUserId(UUID userId) {
    return repository.findByUserId(userId).stream().map(this::toDomain).toList();
  }

  @Override
  public RefreshSession save(RefreshSession session) {
    repository.save(toEntity(session));
    return session;
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    repository.deleteAllByUserId(userId);
  }

  private RefreshSession toDomain(RefreshSessionJpaEntity entity) {
    return new RefreshSession(
        entity.getId(),
        entity.getUserId(),
        entity.getTokenHash(),
        entity.getExpiresAt(),
        entity.getRevokedAt(),
        entity.getCreatedAt());
  }

  private RefreshSessionJpaEntity toEntity(RefreshSession session) {
    return new RefreshSessionJpaEntity(
        session.getId(),
        session.getUserId(),
        session.getTokenHash(),
        session.getExpiresAt(),
        session.getRevokedAt(),
        session.getCreatedAt());
  }
}
