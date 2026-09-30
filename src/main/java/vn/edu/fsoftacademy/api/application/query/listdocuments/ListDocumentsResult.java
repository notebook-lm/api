package vn.edu.fsoftacademy.api.application.query.listdocuments;

import java.util.List;
import vn.edu.fsoftacademy.api.domain.entity.ProjectDocument;

public record ListDocumentsResult(
    List<ProjectDocument> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {}
