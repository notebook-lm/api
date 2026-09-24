package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.util.List;
import vn.edu.fsoftacademy.api.domain.entity.Project;

public record ListProjectsResult(
    List<Project> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {}
