package vn.edu.fsoftacademy.api.api.rest.project.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
    UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
