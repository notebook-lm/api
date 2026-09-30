package vn.edu.fsoftacademy.api.api.rest.document.dto.response;

import java.util.List;

public record ProjectDocumentPageResponse(
    List<ProjectDocumentResponse> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {}
