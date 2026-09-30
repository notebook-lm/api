package vn.edu.fsoftacademy.api.infrastructure.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;

@Component
public class BcryptPasswordHasher implements PasswordHasher {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public String hash(String raw) {
    return encoder.encode(raw);
  }

  public boolean matches(String raw, String hash) {
    return encoder.matches(raw, hash);
  }
}
