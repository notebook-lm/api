package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtAccessTokenAdapterTest {
  private static final String SECRET =
      "bG9jYWwtZGV2ZWxvcG1lbnQtc2VjcmV0LW11c3QtYmUtYXQtbGVhc3QtMzItYnl0ZXM=";
  private final JwtAccessTokenAdapter accessTokens =
      new JwtAccessTokenAdapter(
          new JwtProperties(SECRET, Duration.ofMinutes(15), Duration.ofDays(30)));

  @Test
  void issuesValidBearerTokenWithConfiguredTtl() {
    UUID userId = UUID.randomUUID();

    var token =
        accessTokens.issue(
            userId, "user@example.com", java.util.List.of("USER"), java.util.List.of("note:read"));

    assertEquals("Bearer", token.tokenType());
    assertEquals(900L, token.expiresIn());
    assertTrue(accessTokens.isValid(token.value()));
    assertEquals(userId, accessTokens.extractUserId(token.value()));
  }

  @Test
  void extractsRoleAndPermissionClaimsAsSpringAuthorities() {
    var token =
        accessTokens.issue(
            UUID.randomUUID(),
            "user@example.com",
            java.util.List.of("USER"),
            java.util.List.of("user:self:read", "user:self:update"));

    assertEquals(
        java.util.Set.of("ROLE_USER", "user:self:read", "user:self:update"),
        java.util.Set.copyOf(accessTokens.extractAuthorities(token.value())));
  }

  @Test
  void rejectsMalformedTamperedAndForeignTokens() {
    var token =
        accessTokens.issue(
            UUID.randomUUID(), "user@example.com", java.util.List.of("USER"), java.util.List.of());
    var otherAccessTokens =
        new JwtAccessTokenAdapter(
            new JwtProperties(
                "YW5vdGhlci1kZXZlbG9wbWVudC1zZWNyZXQtbXVzdC1iZS1hdC1sZWFzdC0zMi1ieXRlcw==",
                Duration.ofMinutes(15),
                Duration.ofDays(30)));
    var foreignToken =
        otherAccessTokens.issue(
            UUID.randomUUID(), "user@example.com", java.util.List.of("USER"), java.util.List.of());

    assertFalse(accessTokens.isValid("not-a-jwt"));
    assertFalse(accessTokens.isValid(token.value() + "tampered"));
    assertFalse(accessTokens.isValid(foreignToken.value()));
  }
}
