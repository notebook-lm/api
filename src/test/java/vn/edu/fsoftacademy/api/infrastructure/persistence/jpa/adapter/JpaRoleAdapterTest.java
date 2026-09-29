package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.PermissionJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.RoleJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.RoleJpaRepository;

class JpaRoleAdapterTest {
  @Test
  void mapsRoleAndPermissionsToDomain() {
    var repository = mock(RoleJpaRepository.class);
    var roleId = UUID.randomUUID();
    var permissionId = UUID.randomUUID();
    when(repository.findByName("USER")).thenReturn(Optional.of(new RoleJpaEntity(roleId, "USER", Set.of(new PermissionJpaEntity(permissionId, "project:read")))));

    var result = new JpaRoleAdapter(repository).findByName("USER");

    assertTrue(result.isPresent());
    assertEquals(roleId, result.orElseThrow().getId());
    assertEquals("USER", result.orElseThrow().getName());
    assertEquals(Set.of("project:read"), result.orElseThrow().getPermissionCodes());
  }

  @Test
  void preservesMissingRole() {
    var repository = mock(RoleJpaRepository.class);
    when(repository.findByName("UNKNOWN")).thenReturn(Optional.empty());
    assertTrue(new JpaRoleAdapter(repository).findByName("UNKNOWN").isEmpty());
  }
}
