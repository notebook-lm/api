package vn.edu.fsoftacademy.api.application.port;

import vn.edu.fsoftacademy.api.application.model.RefreshToken;

public interface RefreshTokenPort {
  RefreshToken issue();

  String hash(String rawToken);
}
