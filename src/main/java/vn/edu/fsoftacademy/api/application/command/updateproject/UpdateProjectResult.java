package vn.edu.fsoftacademy.api.application.command.updateproject;

import java.time.Instant;
import java.util.UUID;

public record UpdateProjectResult(
    UUID id, String title, String description, Instant createdAt, Instant updatedAt) {}
