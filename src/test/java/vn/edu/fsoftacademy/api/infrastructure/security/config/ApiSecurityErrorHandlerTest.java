package vn.edu.fsoftacademy.api.infrastructure.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    assertEquals(401, response.getStatus());
    assertEquals("Authentication is required", objectMapper.readTree(response.getContentAsByteArray()).get("message").asText());
  }

  @Test
  void writesApiErrorForForbiddenRequests() throws Exception {
    var response = new MockHttpServletResponse();

    new ApiAccessDeniedHandler(authenticationEntryPoint)
        .handle(new MockHttpServletRequest(), response, new AccessDeniedException("Denied"));

    assertEquals(403, response.getStatus());
    assertEquals("You do not have permission to access this resource", objectMapper.readTree(response.getContentAsByteArray()).get("message").asText());
  }
}
