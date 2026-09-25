package vn.edu.fsoftacademy.api.infrastructure.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

class ApiSecurityErrorHandlerTest {
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  private final ApiAuthenticationEntryPoint authenticationEntryPoint =
      new ApiAuthenticationEntryPoint();

  @Test
  void writesApiErrorForUnauthenticatedRequests() throws Exception {
    var response = new MockHttpServletResponse();

    authenticationEntryPoint.commence(
        new MockHttpServletRequest(), response, mock(AuthenticationException.class));

    assertSecurityError(
        response,
        401,
        "Unauthorized",
        "AUTHENTICATION_REQUIRED",
        "Authentication is required");
  }

  @Test
  void writesApiErrorForForbiddenRequests() throws Exception {
    var response = new MockHttpServletResponse();

    new ApiAccessDeniedHandler(authenticationEntryPoint)
        .handle(new MockHttpServletRequest(), response, new AccessDeniedException("Denied"));

    assertSecurityError(
        response,
        403,
        "Forbidden",
        "ACCESS_DENIED",
        "You do not have permission to access this resource");
  }

  private void assertSecurityError(
      MockHttpServletResponse response,
      int status,
      String error,
      String code,
      String message)
      throws Exception {
    assertEquals(status, response.getStatus());
    assertTrue(response.getContentType().startsWith("application/json"));
    var payload = objectMapper.readTree(response.getContentAsByteArray());
    assertTrue(payload.hasNonNull("timestamp"));
    assertTrue(payload.get("timestamp").isTextual());
    assertEquals(status, payload.get("status").asInt());
    assertEquals(error, payload.get("error").asText());
    assertEquals(code, payload.get("code").asText());
    assertEquals(message, payload.get("message").asText());
    assertTrue(payload.get("fieldErrors").isObject());
    assertTrue(payload.get("fieldErrors").isEmpty());
  }
}
