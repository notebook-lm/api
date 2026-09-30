package vn.edu.fsoftacademy.api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.model.AccessToken;
import vn.edu.fsoftacademy.api.application.port.AccessTokenPort;
import vn.edu.fsoftacademy.api.application.port.RefreshTokenPort;
import vn.edu.fsoftacademy.api.application.repository.RefreshSessionRepository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.RefreshSession;
import vn.edu.fsoftacademy.api.domain.entity.User;

class SessionTokenServiceTest {
  private final InMemoryRefreshSessionRepository sessions = new InMemoryRefreshSessionRepository();
  private final User user =
      new User(
          UUID.randomUUID(),
          "user@example.com",
          "User",
          "hash",
          true,
          Instant.now(),
          Instant.now());
  private final SessionTokenService service =
      new SessionTokenService(
          new FakeAccessTokens(),
          new FakeRefreshTokens(),
          sessions,
          new SingleUserRepository(user));

  @Test
  void issuesAccessTokenAndPersistsOnlyRefreshTokenHash() {
    var issued = service.issue(user);

    assertEquals("access-" + user.getId(), issued.accessToken().value());
    assertEquals("refresh-1", issued.refreshToken().value());
    assertEquals("hash-refresh-1", sessions.tokens.getFirst().getTokenHash());
    assertNotEquals(issued.refreshToken().value(), sessions.tokens.getFirst().getTokenHash());
    assertEquals(user.getId(), sessions.tokens.getFirst().getUserId());
    assertTrue(sessions.tokens.getFirst().getExpiresAt().isAfter(Instant.now()));
  }

  @Test
  void rotatesActiveRefreshSessionAndRevokesTheOldOne() {
    String oldRefreshToken = service.issue(user).refreshToken().value();

    var rotated = service.rotate(oldRefreshToken);

    assertEquals("refresh-2", rotated.refreshToken().value());
    assertTrue(sessions.tokens.getFirst().getRevokedAt() != null);
    assertEquals(2, sessions.tokens.size());
  }

  @Test
  void rejectsUnknownOrRevokedRefreshSessions() {
    assertThrows(InvalidRefreshTokenException.class, () -> service.rotate("unknown"));

    String refreshToken = service.issue(user).refreshToken().value();
    service.revoke(refreshToken);

    assertThrows(InvalidRefreshTokenException.class, () -> service.rotate(refreshToken));
  }

  @Test
  void rejectsExpiredRefreshSessionWithoutIssuingANewSession() {
    sessions.save(new RefreshSession(user.getId(), "hash-expired", Instant.now().minusSeconds(1)));

    assertThrows(InvalidRefreshTokenException.class, () -> service.rotate("expired"));

    assertEquals(1, sessions.tokens.size());
    assertEquals(null, sessions.tokens.getFirst().getRevokedAt());
  }

  @Test
  void rejectsRefreshSessionWhenItsUserNoLongerExists() {
    UUID removedUserId = UUID.randomUUID();
    RefreshSession session =
        new RefreshSession(removedUserId, "hash-removed-user", Instant.now().plusSeconds(3600));
    sessions.save(session);

    assertThrows(InvalidRefreshTokenException.class, () -> service.rotate("removed-user"));

    assertEquals(null, session.getRevokedAt());
    assertEquals(1, sessions.tokens.size());
  }

  @Test
  void revokingUnknownTokenIsASafeNoOp() {
    service.revoke("unknown");

    assertTrue(sessions.tokens.isEmpty());
  }

  @Test
  void revokeAllOnlyAffectsSessionsForRequestedUser() {
    User anotherUser = new User("other@example.com", "Other", "hash");
    service.issue(user);
    service.issue(anotherUser);

    service.revokeAll(user.getId());

    assertTrue(
        sessions.tokens.stream()
            .filter(session -> session.getUserId().equals(user.getId()))
            .noneMatch(RefreshSession::isActive));
    assertTrue(
        sessions.tokens.stream()
            .filter(session -> session.getUserId().equals(anotherUser.getId()))
            .allMatch(RefreshSession::isActive));
  }

  @Test
  void revokesOrDeletesEverySessionForUser() {
    service.issue(user);
    service.issue(user);

    service.revokeAll(user.getId());
    assertTrue(sessions.tokens.stream().noneMatch(RefreshSession::isActive));

    service.deleteAll(user.getId());
    assertTrue(sessions.tokens.isEmpty());
  }

  private static class FakeAccessTokens implements AccessTokenPort {
    public AccessToken issue(
        UUID userId,
        String email,
        java.util.Collection<String> roles,
        java.util.Collection<String> permissions) {
      return new AccessToken("access-" + userId, "Bearer", 900);
    }

    public boolean isValid(String token) {
      return true;
    }

    public UUID extractUserId(String token) {
      throw new UnsupportedOperationException();
    }

    public java.util.Collection<String> extractAuthorities(String token) {
      return java.util.List.of();
    }
  }

  private static class FakeRefreshTokens implements RefreshTokenPort {
    private int sequence;

    public vn.edu.fsoftacademy.api.application.model.RefreshToken issue() {
      return new vn.edu.fsoftacademy.api.application.model.RefreshToken(
          "refresh-" + ++sequence, 3600);
    }

    public String hash(String rawToken) {
      return "hash-" + rawToken;
    }
  }

  private static class InMemoryRefreshSessionRepository implements RefreshSessionRepository {
    private final List<RefreshSession> tokens = new ArrayList<>();

    public Optional<RefreshSession> findByTokenHash(String hash) {
      return tokens.stream().filter(token -> token.getTokenHash().equals(hash)).findFirst();
    }

    public List<RefreshSession> findByUserId(UUID userId) {
      return tokens.stream().filter(token -> token.getUserId().equals(userId)).toList();
    }

    public RefreshSession save(RefreshSession token) {
      if (!tokens.contains(token)) tokens.add(token);
      return token;
    }

    public void deleteAllByUserId(UUID userId) {
      tokens.removeIf(token -> token.getUserId().equals(userId));
    }
  }

  private static class SingleUserRepository implements UserRepository {
    private final User user;

    private SingleUserRepository(User user) {
      this.user = user;
    }

    public Optional<User> findById(UUID id) {
      return user.getId().equals(id) ? Optional.of(user) : Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
      return Optional.empty();
    }

    public boolean existsByEmail(String email) {
      return false;
    }

    public User save(User savedUser) {
      return savedUser;
    }

    public void delete(User deletedUser) {}
  }
}
