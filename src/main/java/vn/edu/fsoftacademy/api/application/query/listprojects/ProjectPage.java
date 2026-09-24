package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.util.List;

public record ProjectPage(
    List<ListProjectsResult> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {}
