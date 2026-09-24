package vn.edu.fsoftacademy.api.application.command.createproject;

import java.time.Instant;
import java.util.UUID;

public record CreateProjectResult(
    UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
