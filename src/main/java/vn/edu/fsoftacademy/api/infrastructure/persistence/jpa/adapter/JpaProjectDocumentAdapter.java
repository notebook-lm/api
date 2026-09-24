package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.*;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectDocumentJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectDocumentJpaRepository;

@Repository
public class JpaProjectDocumentAdapter implements ProjectDocumentRepository {
  private final ProjectDocumentJpaRepository documents;

  public JpaProjectDocumentAdapter(ProjectDocumentJpaRepository documents) {
    this.documents = documents;
  }

  public ProjectDocument save(ProjectDocument d) {
    documents.save(entity(d));
    return d;
  }

  public List<ProjectDocument> findAllByProjectId(UUID id) {
    return documents.findAllByProjectIdOrderByCreatedAtDesc(id).stream().map(this::domain).toList();
  }

  public Optional<ProjectDocument> findByIdAndProjectId(UUID id, UUID projectId) {
    return documents.findByIdAndProjectId(id, projectId).map(this::domain);
  }

  public void delete(ProjectDocument d) {
    documents.deleteById(d.getId());
  }

  private ProjectDocument domain(ProjectDocumentJpaEntity e) {
    return new ProjectDocument(
        e.getId(),
        e.getProjectId(),
        e.getTitle(),
        e.getOriginalFilename(),
        e.getContentType(),
        e.getSizeBytes(),
        e.getObjectKey(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  private ProjectDocumentJpaEntity entity(ProjectDocument d) {
    return new ProjectDocumentJpaEntity(
        d.getId(),
        d.getProjectId(),
        d.getTitle(),
        d.getOriginalFilename(),
        d.getContentType(),
        d.getSizeBytes(),
        d.getObjectKey(),
        d.getCreatedAt(),
        d.getUpdatedAt());
  }
}
