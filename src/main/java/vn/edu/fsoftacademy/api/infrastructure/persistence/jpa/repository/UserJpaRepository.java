package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
  Optional<UserJpaEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
