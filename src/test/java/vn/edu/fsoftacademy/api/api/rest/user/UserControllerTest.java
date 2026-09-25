package vn.edu.fsoftacademy.api.api.rest.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.edu.fsoftacademy.api.api.rest.shared.error.AuthenticationExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.shared.error.BusinessExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ValidationExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.user.dto.request.*;
import vn.edu.fsoftacademy.api.application.command.changeemail.*;
import vn.edu.fsoftacademy.api.application.command.changepassword.*;
import vn.edu.fsoftacademy.api.application.command.deleteaccount.*;
import vn.edu.fsoftacademy.api.application.command.updateprofile.*;
import vn.edu.fsoftacademy.api.application.query.currentuser.*;

class UserControllerTest {
  private final UUID userId = UUID.randomUUID();
  private GetCurrentUserQueryHandler get;
  private UpdateProfileCommandHandler update;
  private ChangeEmailCommandHandler email;
  private ChangePasswordCommandHandler password;
  private DeleteAccountCommandHandler delete;
  private UserController controller;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    get = mock(GetCurrentUserQueryHandler.class);
    update = mock(UpdateProfileCommandHandler.class);
    email = mock(ChangeEmailCommandHandler.class);
    password = mock(ChangePasswordCommandHandler.class);
    delete = mock(DeleteAccountCommandHandler.class);
    controller = new UserController(get, update, email, password, delete);
    mvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(
                new AuthenticationExceptionHandler(),
                new BusinessExceptionHandler(),
                new ValidationExceptionHandler())
            .build();
  }

  @Test
  void getMeMapsCurrentUserQuery() {
    when(get.handle(any()))
        .thenReturn(new GetCurrentUserResult(userId, "user@example.com", "User"));

    var response = controller.getMe(userId);

    assertEquals("user@example.com", response.email());
    verify(get).handle(new GetCurrentUserQuery(userId));
  }

  @Test
  void updateProfileMapsUpdateCommand() {
    when(update.execute(any(), any()))
        .thenReturn(new UpdateProfileResult(userId, "user@example.com", "New Name"));

    var response = controller.updateProfile(userId, new UpdateProfileRequest("New Name"));

    assertEquals("New Name", response.displayName());
    verify(update).execute(userId, new UpdateProfileCommand("New Name"));
  }

  @Test
  void changeEmailMapsChangeEmailCommand() {
    when(email.execute(any(), any()))
        .thenReturn(new ChangeEmailResult(userId, "new@example.com", "User"));

    var response =
        controller.changeEmail(
            userId, new ChangeEmailRequest("new@example.com", "current-password"));

    assertEquals("new@example.com", response.email());
    verify(email).execute(userId, new ChangeEmailCommand("new@example.com", "current-password"));
  }

  @Test
  void changePasswordMapsChangePasswordCommand() {
    controller.changePassword(
        userId, new ChangePasswordRequest("current-password", "new-password"));

    verify(password).execute(userId, new ChangePasswordCommand("current-password", "new-password"));
  }

  @Test
  void deleteAccountMapsDeleteAccountCommand() {
    controller.deleteAccount(userId, new DeleteAccountRequest("current-password"));

    verify(delete).execute(userId, new DeleteAccountCommand("current-password"));
  }

  @Test
  void changeEmailRejectsInvalidRequest() throws Exception {
    mvc.perform(
            patch("/api/v1/users/me/email")
                .principal(userId::toString)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"bad\",\"currentPassword\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

    verifyNoInteractions(email);
  }

  @Test
  void updateProfileRejectsInvalidRequest() throws Exception {
    mvc.perform(
            patch("/api/v1/users/me")
                .principal(userId::toString)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

    verifyNoInteractions(update);
  }
}
