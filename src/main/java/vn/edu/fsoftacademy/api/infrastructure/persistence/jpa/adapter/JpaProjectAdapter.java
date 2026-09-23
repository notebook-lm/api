package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ProjectJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ProjectJpaRepository;

@Repository
public class JpaProjectAdapter implements ProjectRepository {
  private final ProjectJpaRepository projects;
  public JpaProjectAdapter(ProjectJpaRepository projects) { this.projects = projects; }
  public Project save(Project project) { projects.save(toEntity(project)); return project; }
  public List<Project> findAllByOwnerId(UUID ownerId) { return projects.findAllByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(this::toDomain).toList(); }
  public Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId) { return projects.findByIdAndOwnerId(id, ownerId).map(this::toDomain); }
  public void delete(Project project) { projects.deleteById(project.getId()); }
  private Project toDomain(ProjectJpaEntity entity) { return new Project(entity.getId(), entity.getOwnerId(), entity.getTitle(), entity.getDescription(), entity.getCreatedAt(), entity.getUpdatedAt()); }
  private ProjectJpaEntity toEntity(Project project) { return new ProjectJpaEntity(project.getId(), project.getOwnerId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getUpdatedAt()); }
}
