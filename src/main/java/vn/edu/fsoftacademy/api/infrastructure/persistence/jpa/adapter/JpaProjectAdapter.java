package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQuery;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsResult;
import vn.edu.fsoftacademy.api.application.query.listprojects.ProjectPage;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectJpaRepository;

@Repository
public class JpaProjectAdapter implements ProjectRepository {
  private final ProjectJpaRepository projects;

  public JpaProjectAdapter(ProjectJpaRepository projects) {
    this.projects = projects;
  }

  public Project save(Project project) {
    projects.save(toEntity(project));
    return project;
  }

  public ProjectPage findPageByOwnerId(UUID ownerId, ListProjectsQuery query) {
    String property =
        switch (query.sortBy()) {
          case CREATED_AT -> "createdAt";
          case UPDATED_AT -> "updatedAt";
          case TITLE -> "title";
        };
    Sort.Direction direction =
        query.direction()
                == vn.edu.fsoftacademy.api.application.query.listprojects.SortDirection.ASC
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
    var pageable =
        PageRequest.of(
            query.page(),
            query.size(),
            Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id")));
    var result = projects.findAll(specification(ownerId, query), pageable);
    return new ProjectPage(
        result.getContent().stream().map(this::toResult).toList(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages(),
        result.hasNext(),
        result.hasPrevious());
  }

  public Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId) {
    return projects.findByIdAndOwnerId(id, ownerId).map(this::toDomain);
  }

  public void delete(Project project) {
    projects.deleteById(project.getId());
  }

  private Specification<ProjectJpaEntity> specification(UUID ownerId, ListProjectsQuery query) {
    return (root, criteriaQuery, builder) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(builder.equal(root.get("ownerId"), ownerId));
      if (query.query() != null) {
        String pattern = "%" + query.query().toLowerCase() + "%";
        predicates.add(
            builder.or(
                builder.like(builder.lower(root.get("title")), pattern),
                builder.like(
                    builder.lower(builder.coalesce(root.get("description"), "")), pattern)));
      }
      if (query.createdFrom() != null)
        predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), query.createdFrom()));
      if (query.createdTo() != null)
        predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), query.createdTo()));
      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private ListProjectsResult toResult(ProjectJpaEntity entity) {
    return new ListProjectsResult(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  private Project toDomain(ProjectJpaEntity entity) {
    return new Project(
        entity.getId(),
        entity.getOwnerId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  private ProjectJpaEntity toEntity(Project project) {
    return new ProjectJpaEntity(
        project.getId(),
        project.getOwnerId(),
        project.getTitle(),
        project.getDescription(),
        project.getCreatedAt(),
        project.getUpdatedAt());
  }
}
