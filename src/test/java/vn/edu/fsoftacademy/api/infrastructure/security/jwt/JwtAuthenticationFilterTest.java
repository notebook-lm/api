package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

  @ParameterizedTest
  @MethodSource("publicAuthenticationEndpoints")
  void skipsTokenValidationForExplicitPublicAuthenticationEndpoints(String path) throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    var request = request("POST", path, "expired");
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    verify(accessTokens, never()).validationResult(anyString());
    assertEquals(200, response.getStatus());
  }

  @Test
  void doesNotBypassUnknownAuthenticationRoute() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.validationResult("expired"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.EXPIRED);
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request("POST", "/api/v1/auth/future-endpoint", "expired"), response, chain);

    assertError(response, ApiErrorCode.ACCESS_TOKEN_EXPIRED);
    verifyNoInteractions(chain);
  }

  @Test
  void doesNotBypassPublicAuthenticationPathWhenMethodDoesNotMatch() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.validationResult("bad"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.INVALID);
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request("GET", "/api/v1/auth/refresh", "bad"), response, chain);

    assertError(response, ApiErrorCode.ACCESS_TOKEN_INVALID);
    verifyNoInteractions(chain);
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
    var request = request("GET", "/api/v1/users/me", "token");
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
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request("GET", "/api/v1/users/me", "bad"), response, chain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertError(response, ApiErrorCode.ACCESS_TOKEN_INVALID);
    verifyNoInteractions(chain);
  }

  @Test
  void rejectsExpiredBearerTokenWithExpiryCode() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    when(accessTokens.validationResult("expired"))
        .thenReturn(JwtAccessTokenAdapter.TokenValidationResult.EXPIRED);
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request("GET", "/api/v1/users/me", "expired"), response, chain);

    assertError(response, ApiErrorCode.ACCESS_TOKEN_EXPIRED);
    verifyNoInteractions(chain);
  }

  @Test
  void continuesWithoutBearerTokenSoSecurityCanRequireAuthentication() throws Exception {
    JwtAccessTokenAdapter accessTokens = mock(JwtAccessTokenAdapter.class);
    var request = new MockHttpServletRequest("GET", "/api/v1/users/me");
    var response = new MockHttpServletResponse();
    var chain = mock(jakarta.servlet.FilterChain.class);

    new JwtAuthenticationFilter(accessTokens, new ApiAuthenticationEntryPoint())
        .doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    verifyNoInteractions(accessTokens);
    assertEquals(200, response.getStatus());
  }

  private static Stream<Arguments> publicAuthenticationEndpoints() {
    return Stream.of(
        Arguments.of("/api/v1/auth/register"),
        Arguments.of("/api/v1/auth/login"),
        Arguments.of("/api/v1/auth/refresh"),
        Arguments.of("/api/v1/auth/logout"));
  }

  private MockHttpServletRequest request(String method, String path, String token) {
    var request = new MockHttpServletRequest(method, path);
    request.addHeader("Authorization", "Bearer " + token);
    return request;
  }

  private void assertError(MockHttpServletResponse response, ApiErrorCode code) throws Exception {
    assertEquals(401, response.getStatus());
    assertEquals("application/json", response.getContentType());
    assertTrue(response.getContentAsString().contains(code.name()));
  }
}
