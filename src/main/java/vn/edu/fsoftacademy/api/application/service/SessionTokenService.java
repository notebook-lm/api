package vn.edu.fsoftacademy.api.application.service;

import java.time.Instant;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.model.AccessToken;
import vn.edu.fsoftacademy.api.application.model.RefreshToken;
import vn.edu.fsoftacademy.api.application.model.SessionTokens;
import vn.edu.fsoftacademy.api.application.port.AccessTokenPort;
import vn.edu.fsoftacademy.api.application.port.RefreshTokenPort;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.repository.RefreshSessionRepository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.RefreshSession;
import vn.edu.fsoftacademy.api.domain.entity.User;

public class SessionTokenService implements SessionTokenPort {
  private final AccessTokenPort accessTokens;
  private final RefreshTokenPort refreshTokens;
  private final RefreshSessionRepository sessions;
  private final UserRepository users;

  public SessionTokenService(
      AccessTokenPort accessTokens,
      RefreshTokenPort refreshTokens,
      RefreshSessionRepository sessions,
      UserRepository users) {
    this.accessTokens = accessTokens;
    this.refreshTokens = refreshTokens;
    this.sessions = sessions;
    this.users = users;
  }

  @Override
  public SessionTokens issue(User user) {
    RefreshToken refreshToken = refreshTokens.issue();
    sessions.save(
        new RefreshSession(
            user.getId(),
            refreshTokens.hash(refreshToken.value()),
            Instant.now().plusSeconds(refreshToken.expiresIn())));
    return sessionTokens(user, refreshToken);
  }

  @Override
  public SessionTokens rotate(String rawRefreshToken) {
    RefreshSession session =
        sessions
            .findByTokenHash(refreshTokens.hash(rawRefreshToken))
            .orElseThrow(InvalidRefreshTokenException::new);
    if (!session.isActive()) {
      throw new InvalidRefreshTokenException();
    }
    User user = users.findById(session.getUserId()).orElseThrow(InvalidRefreshTokenException::new);
    session.revoke();
    sessions.save(session);
    return issue(user);
  }

  @Override
  public void revoke(String rawRefreshToken) {
    sessions
        .findByTokenHash(refreshTokens.hash(rawRefreshToken))
        .ifPresent(
            session -> {
              session.revoke();
              sessions.save(session);
            });
  }

  @Override
  public void revokeAll(UUID userId) {
    sessions
        .findByUserId(userId)
        .forEach(
            session -> {
              session.revoke();
              sessions.save(session);
            });
  }

  @Override
  public void deleteAll(UUID userId) {
    sessions.deleteAllByUserId(userId);
  }

  private SessionTokens sessionTokens(User user, RefreshToken refreshToken) {
    AccessToken accessToken =
        accessTokens.issue(
            user.getId(), user.getEmail(), user.getRoleNames(), user.getPermissionCodes());
    return new SessionTokens(user, accessToken, refreshToken);
  }
}
