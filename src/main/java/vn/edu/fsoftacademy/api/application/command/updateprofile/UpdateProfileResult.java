package vn.edu.fsoftacademy.api.application.command.updateprofile;

import java.util.UUID;

public record UpdateProfileResult(UUID id, String email, String displayName) {}
