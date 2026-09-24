package vn.edu.fsoftacademy.api.application.port;

import java.util.Collection;
import java.util.UUID;
import vn.edu.fsoftacademy.api.application.model.AccessToken;

public interface AccessTokenPort {
  AccessToken issue(
      UUID userId, String email, Collection<String> roles, Collection<String> permissions);

  boolean isValid(String token);

  UUID extractUserId(String token);

  Collection<String> extractAuthorities(String token);
}
