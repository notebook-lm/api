package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiErrorCode;
import vn.edu.fsoftacademy.api.infrastructure.security.config.ApiAuthenticationEntryPoint;

class JwtAuthenticationFilterTest {
  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void authenticatesValidBearerTokenAndContinuesChain() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    UUID userId = UUID.randomUUID();
    when(accessTokens.validationResult("token"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.VALID);
    when(accessTokens.extractUserId("token")).thenReturn(userId);
    when(accessTokens.extractAuthorities("token"))
        .thenReturn(java.util.List.of("ROLE_USER", "note:read"));
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request, new MockHttpServletResponse(), chain);

    assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    assertTrue(
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    verify(chain).doFilter(any(), any());
  }

  @Test
  void rejectsInvalidBearerTokenWithoutContinuingChain() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.validationResult("bad"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.INVALID);
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer bad");
    var chain = mock(jakarta.servlet.FilterChain.class);

    var response = new MockHttpServletResponse();
    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request, response, chain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(401, response.getStatus());
    assertTrue(response.getContentAsString().contains(ApiErrorCode.ACCESS_TOKEN_INVALID.name()));
    verifyNoInteractions(chain);
  }

  @Test
  void rejectsExpiredBearerTokenWithExpiryCode() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.validationResult("expired"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.EXPIRED);
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer expired");
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request, response, chain);

    assertEquals(401, response.getStatus());
    assertTrue(response.getContentAsString().contains(ApiErrorCode.ACCESS_TOKEN_EXPIRED.name()));
    verifyNoInteractions(chain);
  }
}
