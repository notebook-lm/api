package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectDocumentJpaEntity;

public interface ProjectDocumentJpaRepository
    extends JpaRepository<ProjectDocumentJpaEntity, UUID> {
  List<ProjectDocumentJpaEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

  Optional<ProjectDocumentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
