package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtRefreshTokenAdapterTest {
  private final JwtRefreshTokenAdapter refreshTokens =
      new JwtRefreshTokenAdapter(
          new JwtProperties(
              "bG9jYWwtZGV2ZWxvcG1lbnQtc2VjcmV0LW11c3QtYmUtYXQtbGVhc3QtMzItYnl0ZXM=",
              Duration.ofMinutes(15),
              Duration.ofDays(30)));

  @Test
  void issuesUniqueTokensAndHashesDeterministically() {
    String token = refreshTokens.issue().value();

    assertNotEquals(token, refreshTokens.issue().value());
    assertEquals(refreshTokens.hash(token), refreshTokens.hash(token));
    assertEquals(Duration.ofDays(30).toSeconds(), refreshTokens.issue().expiresIn());
  }
}
