package vn.edu.fsoftacademy.api.domain.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RefreshSessionTest {

  @Test
  void activeSessionIsActiveUntilItIsRevokedOrExpires() {
    var session =
        new RefreshSession(UUID.randomUUID(), "token-hash", Instant.now().plusSeconds(60));

    assertTrue(session.isActive());

    session.revoke();

    assertNotNull(session.getRevokedAt());
    assertFalse(session.isActive());
  }

  @Test
  void expiredSessionIsInactive() {
    var session =
        new RefreshSession(UUID.randomUUID(), "token-hash", Instant.now().minusSeconds(1));

    assertFalse(session.isActive());
  }
}
