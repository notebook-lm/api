package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vn.edu.fsoftacademy.api.domain.entity.User;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.UserJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.UserJpaRepository;

class JpaUserAccountAdapterTest {
  private final UserJpaRepository repository = mock(UserJpaRepository.class);
  private final JpaUserAccountAdapter adapter = new JpaUserAccountAdapter(repository);

  @Test
  void findsAndMapsUserById() {
    UserJpaEntity entity = entity();
    when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

    var user = adapter.findById(entity.getId()).orElseThrow();

    assertEquals(entity.getEmail(), user.getEmail());
    assertEquals(entity.getDisplayName(), user.getDisplayName());
    assertEquals(entity.getPasswordHash(), user.getPasswordHash());
  }

  @Test
  void returnsEmptyWhenUserIsMissing() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());
    assertTrue(adapter.findById(id).isEmpty());
  }

  @Test
  void delegatesEmailLookupAndExistenceCheck() {
    UserJpaEntity entity = entity();
    when(repository.findByEmail("user@example.com")).thenReturn(Optional.of(entity));
    when(repository.existsByEmail("user@example.com")).thenReturn(true);

    assertEquals(entity.getId(), adapter.findByEmail("user@example.com").orElseThrow().getId());
    assertTrue(adapter.existsByEmail("user@example.com"));
  }

  @Test
  void savesMappedEntityAndReturnsOriginalDomainUser() {
    User user = domainUser();

    User saved = adapter.save(user);

    ArgumentCaptor<UserJpaEntity> captor = ArgumentCaptor.forClass(UserJpaEntity.class);
    verify(repository).save(captor.capture());
    assertSame(user, saved);
    assertEquals(user.getId(), captor.getValue().getId());
    assertEquals(user.getEmail(), captor.getValue().getEmail());
    assertEquals(user.getPasswordHash(), captor.getValue().getPasswordHash());
  }

  @Test
  void deletesByDomainUserId() {
    User user = domainUser();
    adapter.delete(user);
    verify(repository).deleteById(user.getId());
  }

  private UserJpaEntity entity() {
    User user = domainUser();
    return new UserJpaEntity(
        user.getId(),
        user.getEmail(),
        user.getDisplayName(),
        user.getPasswordHash(),
        user.isEnabled(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  private User domainUser() {
    Instant now = Instant.now();
    return new User(
        UUID.randomUUID(), "user@example.com", "User", "hash", true, now.minusSeconds(1), now);
  }
}
