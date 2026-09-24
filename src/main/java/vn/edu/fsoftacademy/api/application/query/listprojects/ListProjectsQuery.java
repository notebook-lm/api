package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.time.Instant;

public record ListProjectsQuery(
    String query,
    Instant createdFrom,
    Instant createdTo,
    ProjectSortField sortBy,
    SortDirection direction,
    int page,
    int size) {
  public ListProjectsQuery {
    query = query == null || query.isBlank() ? null : query.strip();
    sortBy = sortBy == null ? ProjectSortField.CREATED_AT : sortBy;
    direction = direction == null ? SortDirection.DESC : direction;
  }
}
