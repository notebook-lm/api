package vn.edu.fsoftacademy.api.api.rest.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.edu.fsoftacademy.api.api.rest.shared.error.AuthenticationExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.shared.error.BusinessExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ValidationExceptionHandler;
import vn.edu.fsoftacademy.api.application.command.login.*;
import vn.edu.fsoftacademy.api.application.command.logout.*;
import vn.edu.fsoftacademy.api.application.command.refreshsession.*;
import vn.edu.fsoftacademy.api.application.command.register.*;

class AuthControllerTest {
  private RegisterCommandHandler register;
  private LoginCommandHandler login;
  private RefreshSessionCommandHandler refresh;
  private LogoutCommandHandler logout;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    register = mock(RegisterCommandHandler.class);
    login = mock(LoginCommandHandler.class);
    refresh = mock(RefreshSessionCommandHandler.class);
    logout = mock(LogoutCommandHandler.class);
    mvc = MockMvcBuilders.standaloneSetup(new AuthController(register, login, refresh, logout))
        .setControllerAdvice(new AuthenticationExceptionHandler(), new BusinessExceptionHandler(), new ValidationExceptionHandler())
        .build();
  }

  @Test
  void registerCreatesAccountAndMapsRegistrationResponse() throws Exception {
    when(register.execute(any())).thenReturn(new RegisterResult(UUID.randomUUID(), "user@example.com", "User"));

    mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"user@example.com\",\"displayName\":\"User\",\"password\":\"secret123\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("user@example.com"))
        .andExpect(jsonPath("$.accessToken").doesNotExist());

    var command = ArgumentCaptor.forClass(RegisterCommand.class);
    verify(register).execute(command.capture());
    assertEquals(new RegisterCommand("user@example.com", "User", "secret123"), command.getValue());
  }

  @Test
  void loginMapsSessionResponseAndInvokesLoginHandler() throws Exception {
    when(login.execute(any())).thenReturn(loginResult());

    mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"user@example.com\",\"password\":\"secret123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

    var command = ArgumentCaptor.forClass(LoginCommand.class);
    verify(login).execute(command.capture());
    assertEquals(new LoginCommand("user@example.com", "secret123"), command.getValue());
  }

  @Test
  void refreshMapsSessionResponseAndInvokesRefreshHandler() throws Exception {
    when(refresh.execute(any())).thenReturn(refreshResult());

    mvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"refresh-token\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));

    verify(refresh).execute(new RefreshSessionCommand("refresh-token"));
  }

  @Test
  void logoutReturnsNoContentAndInvokesLogoutHandler() throws Exception {
    mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"refresh-token\"}"))
        .andExpect(status().isNoContent());

    verify(logout).execute(new LogoutCommand("refresh-token"));
  }

  @Test
  void loginRejectsInvalidRequest() throws Exception {
    mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"bad\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));

    verifyNoInteractions(login);
  }

  private LoginResult loginResult() {
    return new LoginResult(UUID.randomUUID(), "user@example.com", "User", "access-token", "refresh-token", "Bearer", 3600L);
  }

  private RefreshSessionResult refreshResult() {
    return new RefreshSessionResult(UUID.randomUUID(), "user@example.com", "User", "access-token", "refresh-token", "Bearer", 3600L);
  }
}
