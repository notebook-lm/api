package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.model.RefreshToken;
import vn.edu.fsoftacademy.api.application.port.RefreshTokenPort;

@Component
public class JwtRefreshTokenAdapter implements RefreshTokenPort {
  private final SecureRandom random = new SecureRandom();
  private final JwtProperties properties;

  public JwtRefreshTokenAdapter(JwtProperties properties) {
    this.properties = properties;
  }

  @Override
  public RefreshToken issue() {
    byte[] bytes = new byte[64];
    random.nextBytes(bytes);
    return new RefreshToken(
        Base64.getUrlEncoder().withoutPadding().encodeToString(bytes),
        properties.refreshTokenTtl().toSeconds());
  }

  @Override
  public String hash(String rawToken) {
    try {
      return Base64.getEncoder()
          .encodeToString(
              MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }

}
