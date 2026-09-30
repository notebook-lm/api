package vn.edu.fsoftacademy.api.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void createsEnabledUserWithIdentityAndTimestamps() {
    var user = new User("user@example.com", "User", "password-hash");

    assertNotNull(user.getId());
    assertTrue(user.isEnabled());
    assertEquals("user@example.com", user.getEmail());
    assertEquals("User", user.getDisplayName());
    assertEquals("password-hash", user.getPasswordHash());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @Test
  void updatesMutableProfileFieldsWithoutChangingIdentity() {
    UUID id = UUID.randomUUID();
    Instant createdAt = Instant.now().minusSeconds(60);
    Instant updatedAt = Instant.now().minusSeconds(30);
    var user = new User(id, "old@example.com", "Old", "old-hash", true, createdAt, updatedAt);

    user.updateDisplayName("New Name");
    user.updateEmail("new@example.com");
    user.updatePasswordHash("new-hash");

    assertEquals(id, user.getId());
    assertEquals(createdAt, user.getCreatedAt());
    assertEquals("New Name", user.getDisplayName());
    assertEquals("new@example.com", user.getEmail());
    assertEquals("new-hash", user.getPasswordHash());
    assertTrue(user.getUpdatedAt().isAfter(updatedAt));
  }
}
