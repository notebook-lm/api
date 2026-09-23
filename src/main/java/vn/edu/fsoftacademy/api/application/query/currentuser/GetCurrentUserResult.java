package vn.edu.fsoftacademy.api.application.query.currentuser;

import java.util.UUID;

public record GetCurrentUserResult(UUID id, String email, String displayName) {}
