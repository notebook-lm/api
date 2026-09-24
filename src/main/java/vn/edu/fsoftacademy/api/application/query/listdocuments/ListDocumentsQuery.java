package vn.edu.fsoftacademy.api.application.query.listdocuments;

import java.time.Instant;

public record ListDocumentsQuery(
    String query,
    Instant createdFrom,
    Instant createdTo,
    DocumentSortField sortBy,
    DocumentSortDirection direction,
    int page,
    int size) {
  public ListDocumentsQuery {
    query = query == null || query.isBlank() ? null : query.strip();
    sortBy = sortBy == null ? DocumentSortField.CREATED_AT : sortBy;
    direction = direction == null ? DocumentSortDirection.DESC : direction;
  }
}
