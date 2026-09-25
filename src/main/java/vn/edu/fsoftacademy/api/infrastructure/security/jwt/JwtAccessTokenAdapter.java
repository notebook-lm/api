package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.model.AccessToken;
import vn.edu.fsoftacademy.api.application.port.AccessTokenPort;

@Component
public class JwtAccessTokenAdapter implements AccessTokenPort {
  private static final String ROLES_CLAIM = "roles";
  private static final String PERMISSIONS_CLAIM = "permissions";
  private final JwtProperties properties;

  public JwtAccessTokenAdapter(JwtProperties properties) {
    this.properties = properties;
  }

  @Override
  public AccessToken issue(
      UUID userId, String email, Collection<String> roles, Collection<String> permissions) {
    Instant now = Instant.now();
    String token =
        Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim(ROLES_CLAIM, roles)
            .claim(PERMISSIONS_CLAIM, permissions)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(properties.accessTokenTtl())))
            .signWith(signingKey())
            .compact();
    return new AccessToken(token, "Bearer", properties.accessTokenTtl().toSeconds());
  }

  @Override
  public boolean isValid(String token) {
    return validationResult(token) == TokenValidationResult.VALID;
  }

  public TokenValidationResult validationResult(String token) {
    try {
      parse(token);
      return TokenValidationResult.VALID;
    } catch (ExpiredJwtException exception) {
      return TokenValidationResult.EXPIRED;
    } catch (RuntimeException exception) {
      return TokenValidationResult.INVALID;
    }
  }

  @Override
  public UUID extractUserId(String token) {
    return UUID.fromString(parse(token).getSubject());
  }

  @Override
  public Collection<String> extractAuthorities(String token) {
    Claims claims = parse(token);
    var authorities = new LinkedHashSet<String>();
    claimValues(claims, ROLES_CLAIM).forEach(role -> authorities.add("ROLE_" + role));
    authorities.addAll(claimValues(claims, PERMISSIONS_CLAIM));
    return authorities;
  }

  public enum TokenValidationResult {
    VALID,
    EXPIRED,
    INVALID
  }

  private Collection<String> claimValues(Claims claims, String claim) {
    Object value = claims.get(claim);
    if (!(value instanceof Collection<?> values)) return java.util.List.of();
    return values.stream().filter(String.class::isInstance).map(String.class::cast).toList();
  }

  private Claims parse(String token) {
    return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(Base64.getDecoder().decode(properties.secret()));
  }
}
