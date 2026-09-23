package vn.edu.fsoftacademy.api.api.rest.project.dto.response;

import java.util.List;

public record ProjectPageResponse(List<ProjectResponse> items, int page, int size, long totalItems, int totalPages,
    boolean hasNext, boolean hasPrevious) {}
