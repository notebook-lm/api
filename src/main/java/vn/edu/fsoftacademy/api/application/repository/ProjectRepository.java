package vn.edu.fsoftacademy.api.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.domain.entity.Project;

public interface ProjectRepository {
  Project save(Project project);
  List<Project> findAllByOwnerId(UUID ownerId);
  Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId);
  void delete(Project project);
}
