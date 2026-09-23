package vn.edu.fsoftacademy.api.application.port;

import java.util.UUID;
import vn.edu.fsoftacademy.api.application.exception.InvalidRefreshTokenException;
import vn.edu.fsoftacademy.api.application.model.SessionTokens;
import vn.edu.fsoftacademy.api.domain.entity.User;

public interface SessionTokenPort {
  SessionTokens issue(User user);

  SessionTokens rotate(String rawRefreshToken) throws InvalidRefreshTokenException;

  void revoke(String rawRefreshToken);

  void revokeAll(UUID userId);

  void deleteAll(UUID userId);
}
