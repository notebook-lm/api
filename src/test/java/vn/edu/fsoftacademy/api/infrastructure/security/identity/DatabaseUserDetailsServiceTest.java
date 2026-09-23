package vn.edu.fsoftacademy.api.infrastructure.security.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import vn.edu.fsoftacademy.api.domain.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.domain.entity.User;

class DatabaseUserDetailsServiceTest {
  @Test
  void mapsAccountToSpringUserDetails() {
    UserRepository users = mock(UserRepository.class);
    when(users.findByEmail("user@example.com"))
        .thenReturn(Optional.of(userWithRole()));

    var details = new DatabaseUserDetailsService(users).loadUserByUsername("user@example.com");

    assertEquals("user@example.com", details.getUsername());
    assertEquals("hash", details.getPassword());
    assertTrue(details.isEnabled());
    assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }

  private User userWithRole() {
    User user = new User("user@example.com", "User", "hash");
    user.assignRole(new Role("USER"));
    return user;
  }

  @Test
  void throwsStandardExceptionForUnknownUser() {
    UserRepository users = mock(UserRepository.class);
    when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> new DatabaseUserDetailsService(users).loadUserByUsername("missing@example.com"));
  }
}
