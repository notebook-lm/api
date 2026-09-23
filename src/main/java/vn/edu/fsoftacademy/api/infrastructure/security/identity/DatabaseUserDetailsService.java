package vn.edu.fsoftacademy.api.infrastructure.security.identity;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public DatabaseUserDetailsService(UserRepository users) {
    this.users = users;
  }

  public UserDetails loadUserByUsername(String email) {
    var user =
        users
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPasswordHash())
        .disabled(!user.isEnabled())
.authorities(
            java.util.stream.Stream.concat(
                    user.getRoleNames().stream().map(role -> "ROLE_" + role),
                    user.getPermissionCodes().stream())
                .toArray(String[]::new))
        .build();
  }
}
