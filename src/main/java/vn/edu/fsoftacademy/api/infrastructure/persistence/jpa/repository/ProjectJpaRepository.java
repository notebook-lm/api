package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectJpaEntity;

public interface ProjectJpaRepository
    extends JpaRepository<ProjectJpaEntity, UUID>, JpaSpecificationExecutor<ProjectJpaEntity> {
  Optional<ProjectJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
}
