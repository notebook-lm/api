package vn.edu.fsoftacademy.api.application.query.listprojects;

import java.time.Instant;
import java.util.UUID;

public record ListProjectsResult(UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
