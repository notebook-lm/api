package vn.edu.fsoftacademy.api.infrastructure.security.password;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BcryptPasswordHasherTest {
  private final BcryptPasswordHasher hasher = new BcryptPasswordHasher();

  @Test
  void hashesAndVerifiesMatchingPassword() {
    String hash = hasher.hash("secret123");
    assertNotEquals("secret123", hash);
    assertTrue(hasher.matches("secret123", hash));
  }

  @Test
  void rejectsDifferentPassword() {
    assertFalse(hasher.matches("wrong", hasher.hash("secret123")));
  }
}
