package vn.edu.fsoftacademy.api.api.rest.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.fsoftacademy.api.api.rest.auth.dto.request.*;
import vn.edu.fsoftacademy.api.api.rest.auth.dto.response.AuthResponse;
import vn.edu.fsoftacademy.api.api.rest.auth.dto.response.AuthenticatedUserResponse;
import vn.edu.fsoftacademy.api.api.rest.auth.dto.response.RegistrationResponse;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiError;
import vn.edu.fsoftacademy.api.application.command.login.LoginCommand;
import vn.edu.fsoftacademy.api.application.command.login.LoginCommandHandler;
import vn.edu.fsoftacademy.api.application.command.login.LoginResult;
import vn.edu.fsoftacademy.api.application.command.logout.LogoutCommand;
import vn.edu.fsoftacademy.api.application.command.logout.LogoutCommandHandler;
import vn.edu.fsoftacademy.api.application.command.refreshsession.RefreshSessionCommand;
import vn.edu.fsoftacademy.api.application.command.refreshsession.RefreshSessionCommandHandler;
import vn.edu.fsoftacademy.api.application.command.refreshsession.RefreshSessionResult;
import vn.edu.fsoftacademy.api.application.command.register.RegisterCommand;
import vn.edu.fsoftacademy.api.application.command.register.RegisterCommandHandler;
import vn.edu.fsoftacademy.api.application.command.register.RegisterResult;

@RestController
@Tag(
    name = "Authentication",
    description = "Public account registration, login and token lifecycle endpoints.")
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final RegisterCommandHandler register;
  private final LoginCommandHandler login;
  private final RefreshSessionCommandHandler refresh;
  private final LogoutCommandHandler logout;

  public AuthController(
      RegisterCommandHandler register,
      LoginCommandHandler login,
      RefreshSessionCommandHandler refresh,
      LogoutCommandHandler logout) {
    this.register = register;
    this.login = login;
    this.refresh = refresh;
    this.logout = logout;
  }

  @PostMapping("/register")
  @Operation(
      summary = "Register an account",
      description = "Creates an account. Login separately to obtain tokens.")
  @SecurityRequirements
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Account created",
        content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Email already registered",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @ResponseStatus(HttpStatus.CREATED)
  public RegistrationResponse register(@Valid @RequestBody RegisterRequest request) {
    return response(
        register.execute(
            new RegisterCommand(request.email(), request.displayName(), request.password())));
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login",
      description = "Authenticates credentials and issues an access token and refresh token.")
  @SecurityRequirements
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Authenticated",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid credentials",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return response(login.execute(new LoginCommand(request.email(), request.password())));
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh session",
      description = "Rotates a valid refresh token and returns a fresh token pair.")
  @SecurityRequirements
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tokens refreshed",
        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid refresh token",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return response(refresh.execute(new RefreshSessionCommand(request.refreshToken())));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout", description = "Revokes the supplied refresh token.")
  @SecurityRequirements
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Logged out"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody LogoutRequest request) {
    logout.execute(new LogoutCommand(request.refreshToken()));
  }

  private AuthResponse response(LoginResult result) {
    return response(
        result.userId(),
        result.email(),
        result.displayName(),
        result.accessToken(),
        result.refreshToken(),
        result.tokenType(),
        result.expiresIn());
  }

  private RegistrationResponse response(RegisterResult result) {
    return new RegistrationResponse(result.id(), result.email(), result.displayName());
  }

  private AuthResponse response(RefreshSessionResult result) {
    return response(
        result.userId(),
        result.email(),
        result.displayName(),
        result.accessToken(),
        result.refreshToken(),
        result.tokenType(),
        result.expiresIn());
  }

  private AuthResponse response(
      java.util.UUID userId,
      String email,
      String displayName,
      String accessToken,
      String refreshToken,
      String tokenType,
      long expiresIn) {
    return new AuthResponse(
        new AuthenticatedUserResponse(userId, email, displayName),
        accessToken,
        refreshToken,
        tokenType,
        expiresIn);
  }
}
