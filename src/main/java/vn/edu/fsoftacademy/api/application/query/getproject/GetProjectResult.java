package vn.edu.fsoftacademy.api.application.query.getproject;

import java.time.Instant;
import java.util.UUID;

public record GetProjectResult(
    UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
