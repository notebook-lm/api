package vn.edu.fsoftacademy.api.application.command.refreshsession;

import java.util.UUID;

public record RefreshSessionResult(
    UUID userId,
    String email,
    String displayName,
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn) {}
