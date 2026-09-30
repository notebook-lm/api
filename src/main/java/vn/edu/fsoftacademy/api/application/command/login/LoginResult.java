package vn.edu.fsoftacademy.api.application.command.login;

import java.util.UUID;

public record LoginResult(
    UUID userId,
    String email,
    String displayName,
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn) {}
