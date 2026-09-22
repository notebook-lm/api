package vn.edu.fsoftacademy.api.application.command.changeemail;

import java.util.UUID;

public record ChangeEmailResult(UUID id, String email, String displayName) {}
