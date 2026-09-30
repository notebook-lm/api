package vn.edu.fsoftacademy.api.application.repository;

import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQuery;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;
import vn.edu.fsoftacademy.api.domain.entity.Project;

public interface ProjectRepository {
  Project save(Project project);

  PageResult<Project> findPageByOwnerId(UUID ownerId, ListProjectsQuery query);

  Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId);

  void delete(Project project);
}
