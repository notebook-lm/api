package vn.edu.fsoftacademy.api.application.repository;

import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.Project;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQuery;
import vn.edu.fsoftacademy.api.application.query.listprojects.ProjectPage;

public interface ProjectRepository {
  Project save(Project project);
  ProjectPage findPageByOwnerId(UUID ownerId, ListProjectsQuery query);
  Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId);
  void delete(Project project);
}
