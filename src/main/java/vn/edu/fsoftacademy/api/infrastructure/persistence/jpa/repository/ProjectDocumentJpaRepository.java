package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectDocumentJpaEntity;

public interface ProjectDocumentJpaRepository
    extends JpaRepository<ProjectDocumentJpaEntity, UUID>, JpaSpecificationExecutor<ProjectDocumentJpaEntity> {
  List<ProjectDocumentJpaEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

  Optional<ProjectDocumentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
