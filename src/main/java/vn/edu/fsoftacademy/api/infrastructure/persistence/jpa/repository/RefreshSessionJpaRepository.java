package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RefreshSessionJpaEntity;

public interface RefreshSessionJpaRepository extends JpaRepository<RefreshSessionJpaEntity, UUID> {
  Optional<RefreshSessionJpaEntity> findByTokenHash(String tokenHash);

  List<RefreshSessionJpaEntity> findByUserId(UUID userId);

  void deleteAllByUserId(UUID userId);
}
