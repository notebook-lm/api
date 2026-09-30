package vn.edu.fsoftacademy.api.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQuery;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public interface ProjectDocumentRepository {
  ProjectDocument save(ProjectDocument document);

  List<ProjectDocument> findAllByProjectId(UUID projectId);

  PageResult<ProjectDocument> findPageByProjectId(UUID projectId, ListDocumentsQuery query);

  Optional<ProjectDocument> findById(UUID id);

  Optional<ProjectDocument> findByIdAndProjectId(UUID id, UUID projectId);

  void delete(ProjectDocument document);
}
