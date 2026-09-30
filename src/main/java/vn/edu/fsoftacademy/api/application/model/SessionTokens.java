package vn.edu.fsoftacademy.api.application.model;

import vn.edu.fsoftacademy.api.domain.entity.User;

public record SessionTokens(User user, AccessToken accessToken, RefreshToken refreshToken) {}
