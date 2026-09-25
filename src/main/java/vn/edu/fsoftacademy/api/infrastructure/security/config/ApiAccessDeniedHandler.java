package vn.edu.fsoftacademy.api.infrastructure.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class ApiAccessDeniedHandler implements AccessDeniedHandler {
  private final ApiAuthenticationEntryPoint errorWriter;

  public ApiAccessDeniedHandler(ApiAuthenticationEntryPoint errorWriter) {
    this.errorWriter = errorWriter;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    errorWriter.write(
        response, HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_DENIED, "You do not have permission to access this resource");
  }
}
