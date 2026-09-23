package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectJpaEntity;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {
  List<ProjectJpaEntity> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
  Optional<ProjectJpaEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
}
