package vn.edu.fsoftacademy.api.infrastructure.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiError;

public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  public ApiAuthenticationEntryPoint() {
    this.objectMapper = new ObjectMapper().findAndRegisterModules();
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authenticationException)
      throws IOException {
    write(response, HttpStatus.UNAUTHORIZED, "Authentication is required");
  }

  void write(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getOutputStream(),
        new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, Map.of()));
  }
}
