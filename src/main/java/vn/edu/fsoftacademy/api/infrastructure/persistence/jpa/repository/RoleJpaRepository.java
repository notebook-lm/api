package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RoleJpaEntity;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
  Optional<RoleJpaEntity> findByName(String name);
}
