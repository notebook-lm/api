package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {
  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void authenticatesValidBearerTokenAndContinuesChain() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    UUID userId = UUID.randomUUID();
    when(accessTokens.isValid("token")).thenReturn(true);
    when(accessTokens.extractUserId("token")).thenReturn(userId);
    when(accessTokens.extractAuthorities("token"))
        .thenReturn(java.util.List.of("ROLE_USER", "note:read"));
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens)
        .doFilter(request, new MockHttpServletResponse(), chain);

    assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    assertTrue(
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    verify(chain).doFilter(any(), any());
  }

  @Test
  void ignoresMissingOrInvalidBearerTokenAndContinuesChain() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.isValid("bad")).thenReturn(false);
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer bad");
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens)
        .doFilter(request, new MockHttpServletResponse(), chain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(chain).doFilter(any(), any());
  }
}
