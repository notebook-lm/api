package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.query.listdocuments.DocumentSortDirection;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQuery;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectDocumentJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectDocumentJpaRepository;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;

@Repository
public class JpaProjectDocumentAdapter implements ProjectDocumentRepository {
  private final ProjectDocumentJpaRepository documents;

  public JpaProjectDocumentAdapter(ProjectDocumentJpaRepository documents) { this.documents = documents; }

  public ProjectDocument save(ProjectDocument document) { documents.save(entity(document)); return document; }

  public List<ProjectDocument> findAllByProjectId(UUID projectId) {
    return documents.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream().map(this::domain).toList();
  }

  public PageResult<ProjectDocument> findPageByProjectId(UUID projectId, ListDocumentsQuery query) {
    String property = switch (query.sortBy()) { case CREATED_AT -> "createdAt"; case UPDATED_AT -> "updatedAt"; case TITLE -> "title"; };
    Sort.Direction direction = query.direction() == DocumentSortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
    var page = documents.findAll(specification(projectId, query), PageRequest.of(query.page(), query.size(), Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"))));
    return new PageResult<>(page.getContent().stream().map(this::domain).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.hasPrevious());
  }

  public Optional<ProjectDocument> findByIdAndProjectId(UUID id, UUID projectId) { return documents.findByIdAndProjectId(id, projectId).map(this::domain); }
  public void delete(ProjectDocument document) { documents.deleteById(document.getId()); }

  private Specification<ProjectDocumentJpaEntity> specification(UUID projectId, ListDocumentsQuery query) {
    return (root, criteriaQuery, builder) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(builder.equal(root.get("projectId"), projectId));
      if (query.query() != null) { String pattern = "%" + query.query().toLowerCase() + "%"; predicates.add(builder.or(builder.like(builder.lower(root.get("title")), pattern), builder.like(builder.lower(root.get("originalFilename")), pattern))); }
      if (query.createdFrom() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), query.createdFrom()));
      if (query.createdTo() != null) predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), query.createdTo()));
      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
  private ProjectDocument domain(ProjectDocumentJpaEntity e) { return new ProjectDocument(e.getId(), e.getProjectId(), e.getTitle(), e.getOriginalFilename(), e.getContentType(), e.getSizeBytes(), e.getObjectKey(), e.getCreatedAt(), e.getUpdatedAt()); }
  private ProjectDocumentJpaEntity entity(ProjectDocument d) { return new ProjectDocumentJpaEntity(d.getId(), d.getProjectId(), d.getTitle(), d.getOriginalFilename(), d.getContentType(), d.getSizeBytes(), d.getObjectKey(), d.getCreatedAt(), d.getUpdatedAt()); }
}
