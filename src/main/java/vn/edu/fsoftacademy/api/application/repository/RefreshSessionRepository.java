package vn.edu.fsoftacademy.api.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.RefreshSession;

public interface RefreshSessionRepository {
  Optional<RefreshSession> findByTokenHash(String tokenHash);

  List<RefreshSession> findByUserId(UUID userId);

  RefreshSession save(RefreshSession session);

  void deleteAllByUserId(UUID userId);
}
