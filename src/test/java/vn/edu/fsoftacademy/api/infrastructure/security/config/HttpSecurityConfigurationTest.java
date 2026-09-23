package vn.edu.fsoftacademy.api.infrastructure.security.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.fsoftacademy.api.api.rest.auth.AuthController;
import vn.edu.fsoftacademy.api.api.rest.user.UserController;
import vn.edu.fsoftacademy.api.api.rest.project.ProjectController;
import vn.edu.fsoftacademy.api.application.command.changeemail.ChangeEmailCommandHandler;
import vn.edu.fsoftacademy.api.application.command.changepassword.ChangePasswordCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deleteaccount.DeleteAccountCommandHandler;
import vn.edu.fsoftacademy.api.application.command.login.LoginCommandHandler;
import vn.edu.fsoftacademy.api.application.command.logout.LogoutCommandHandler;
import vn.edu.fsoftacademy.api.application.command.refreshsession.RefreshSessionCommandHandler;
import vn.edu.fsoftacademy.api.application.command.register.RegisterCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateprofile.UpdateProfileCommandHandler;
import vn.edu.fsoftacademy.api.application.command.createproject.CreateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateproject.UpdateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deleteproject.DeleteProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.query.getproject.GetProjectQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQueryHandler;
import vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserQueryHandler;
import vn.edu.fsoftacademy.api.infrastructure.security.jwt.JwtAccessTokenAdapter;
import vn.edu.fsoftacademy.api.infrastructure.security.jwt.JwtAuthenticationFilter;

@WebMvcTest(controllers = {AuthController.class, UserController.class, ProjectController.class})
@Import({HttpSecurityConfiguration.class, JwtAuthenticationFilter.class})
class HttpSecurityConfigurationTest {
  @Autowired private MockMvc mvc;

  @MockitoBean private JwtAccessTokenAdapter accessTokens;
  @MockitoBean private RegisterCommandHandler register;
  @MockitoBean private LoginCommandHandler login;
  @MockitoBean private RefreshSessionCommandHandler refresh;
  @MockitoBean private LogoutCommandHandler logout;
  @MockitoBean private GetCurrentUserQueryHandler get;
  @MockitoBean private UpdateProfileCommandHandler update;
  @MockitoBean private ChangeEmailCommandHandler email;
  @MockitoBean private ChangePasswordCommandHandler password;
  @MockitoBean private DeleteAccountCommandHandler delete;
  @MockitoBean private CreateProjectCommandHandler createProject;
  @MockitoBean private ListProjectsQueryHandler listProjects;
  @MockitoBean private GetProjectQueryHandler getProject;
  @MockitoBean private UpdateProjectCommandHandler updateProject;
  @MockitoBean private DeleteProjectCommandHandler deleteProject;

  @Test
  void authEndpointsArePublic() throws Exception {
    mvc.perform(post("/api/v1/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"invalid\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void protectedEndpointRejectsMissingToken() throws Exception {
    mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void protectedEndpointRejectsInvalidToken() throws Exception {
    when(accessTokens.isValid("invalid-token")).thenReturn(false);

    mvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void protectedEndpointsRejectAuthenticatedUsersWithoutRequiredPermission() throws Exception {
    mvc.perform(
            get("/api/v1/users/me")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    UUID.randomUUID(), null, List.of()))))
        .andExpect(status().isForbidden());
  }

  @Test
  void requiredPermissionAllowsControllerInvocation() throws Exception {
    UUID userId = UUID.randomUUID();
    when(get.handle(any())).thenReturn(
        new vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserResult(
            userId, "user@example.com", "User"));

    mvc.perform(
            get("/api/v1/users/me")
                .with(authentication(new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("user:self:read"))))))
        .andExpect(status().isOk());
  }
  @Test
  void projectEndpointsRejectMissingToken() throws Exception {
    mvc.perform(get("/api/v1/projects")).andExpect(status().isUnauthorized());
  }

  @Test
  void projectEndpointsRejectUsersWithoutProjectPermission() throws Exception {
    mvc.perform(get("/api/v1/projects").with(authentication(
        new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null, List.of()))))
        .andExpect(status().isForbidden());
  }

  @Test
  void projectReadPermissionAllowsListInvocation() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listProjects.handle(userId)).thenReturn(List.of());

    mvc.perform(get("/api/v1/projects").with(authentication(
        new UsernamePasswordAuthenticationToken(userId, null,
            List.of(new SimpleGrantedAuthority("project:read"))))))
        .andExpect(status().isOk());
  }
}
