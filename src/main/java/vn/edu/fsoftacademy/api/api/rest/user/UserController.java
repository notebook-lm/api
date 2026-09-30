package vn.edu.fsoftacademy.api.api.rest.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiError;
import vn.edu.fsoftacademy.api.api.rest.user.dto.request.*;
import vn.edu.fsoftacademy.api.api.rest.user.dto.response.UserResponse;
import vn.edu.fsoftacademy.api.application.command.changeemail.ChangeEmailCommand;
import vn.edu.fsoftacademy.api.application.command.changeemail.ChangeEmailCommandHandler;
import vn.edu.fsoftacademy.api.application.command.changeemail.ChangeEmailResult;
import vn.edu.fsoftacademy.api.application.command.changepassword.ChangePasswordCommand;
import vn.edu.fsoftacademy.api.application.command.changepassword.ChangePasswordCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deleteaccount.DeleteAccountCommand;
import vn.edu.fsoftacademy.api.application.command.deleteaccount.DeleteAccountCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateprofile.UpdateProfileCommand;
import vn.edu.fsoftacademy.api.application.command.updateprofile.UpdateProfileCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateprofile.UpdateProfileResult;
import vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserQuery;
import vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserQueryHandler;
import vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserResult;

@RestController
@Tag(name = "User profile", description = "JWT-protected operations for the current account.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/users")
public class UserController {
  private final GetCurrentUserQueryHandler get;
  private final UpdateProfileCommandHandler update;
  private final ChangeEmailCommandHandler email;
  private final ChangePasswordCommandHandler password;
  private final DeleteAccountCommandHandler delete;

  public UserController(
      GetCurrentUserQueryHandler get,
      UpdateProfileCommandHandler update,
      ChangeEmailCommandHandler email,
      ChangePasswordCommandHandler password,
      DeleteAccountCommandHandler delete) {
    this.get = get;
    this.update = update;
    this.email = email;
    this.password = password;
    this.delete = delete;
  }

  @GetMapping("/me")
  @Operation(summary = "Get current profile", description = "Requires `user:self:read`.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Success",
        content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Missing required permission",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PreAuthorize("hasAuthority('user:self:read')")
  public UserResponse getMe(@AuthenticationPrincipal UUID id) {
    return response(get.handle(new GetCurrentUserQuery(id)));
  }

  @PatchMapping("/me")
  @Operation(summary = "Update current profile", description = "Requires `user:self:update`.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Success",
        content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Missing required permission",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PreAuthorize("hasAuthority('user:self:update')")
  public UserResponse updateProfile(
      @AuthenticationPrincipal UUID id, @Valid @RequestBody UpdateProfileRequest request) {
    return response(update.execute(id, new UpdateProfileCommand(request.displayName())));
  }

  @PatchMapping("/me/email")
  @Operation(
      summary = "Change email",
      description = "Requires `user:self:email:update`; revokes active sessions.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Success",
        content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Missing required permission",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PreAuthorize("hasAuthority('user:self:email:update')")
  public UserResponse changeEmail(
      @AuthenticationPrincipal UUID id, @Valid @RequestBody ChangeEmailRequest request) {
    return response(
        email.execute(id, new ChangeEmailCommand(request.email(), request.currentPassword())));
  }

  @PatchMapping("/me/password")
  @Operation(
      summary = "Change password",
      description = "Requires `user:self:password:update`; revokes active sessions.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Success"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Missing required permission",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PreAuthorize("hasAuthority('user:self:password:update')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(
      @AuthenticationPrincipal UUID id, @Valid @RequestBody ChangePasswordRequest request) {
    password.execute(
        id, new ChangePasswordCommand(request.currentPassword(), request.newPassword()));
  }

  @DeleteMapping("/me")
  @Operation(
      summary = "Delete account",
      description = "Requires `user:self:delete`; deletes sessions and account.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Success"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Missing required permission",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PreAuthorize("hasAuthority('user:self:delete')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(
      @AuthenticationPrincipal UUID id, @Valid @RequestBody DeleteAccountRequest request) {
    delete.execute(id, new DeleteAccountCommand(request.currentPassword()));
  }

  private UserResponse response(GetCurrentUserResult result) {
    return response(result.id(), result.email(), result.displayName());
  }

  private UserResponse response(UpdateProfileResult result) {
    return response(result.id(), result.email(), result.displayName());
  }

  private UserResponse response(ChangeEmailResult result) {
    return response(result.id(), result.email(), result.displayName());
  }

  private UserResponse response(UUID id, String email, String displayName) {
    return new UserResponse(id, email, displayName);
  }
}
