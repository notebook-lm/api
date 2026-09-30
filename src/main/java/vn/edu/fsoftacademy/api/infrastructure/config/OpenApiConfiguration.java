package vn.edu.fsoftacademy.api.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class OpenApiConfiguration {
  public static final String BEARER_AUTH = "bearerAuth";
  public static final String ERROR_SCHEMA = "ApiError";

  @Bean
  OpenAPI notebookLmOpenApi(@Value("${server.port:8080}") int port) {
    Components components = new Components()
        .addSecuritySchemes(
            BEARER_AUTH,
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(
                    "Paste only the access token returned by `/api/v1/auth/login` or `/api/v1/auth/refresh`."));

    errorResponses().forEach(components::addResponses);

    return new OpenAPI()
        .info(
            new Info()
                .title("Notebook LM API")
                .version("v1")
                .description(
                    """
                        REST API for authentication, private projects, and project documents.

                        ## Authentication
                        Protected operations require `Authorization: Bearer <access-token>`. Access tokens are short-lived.
                        When a protected request returns `401` with `code: ACCESS_TOKEN_EXPIRED`, refresh the session through
                        `POST /api/v1/auth/refresh` and retry the original request once. Do not refresh for other `401` codes.

                        ## Error response contract
                        Every API error uses `ApiError`: `timestamp`, `status`, `error`, `code`, `message`, and `fieldErrors`.
                        `code` is stable and intended for client logic. `message` is a displayable explanation.
                        `fieldErrors` is populated only for validation failures and maps request fields to messages.
                        """)
                .license(new License().name("Private")))
        .addServersItem(
            new Server().url("http://localhost:" + port).description("Local development"))
        .components(components)
        .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
  }

  @Bean
  OpenApiCustomizer errorResponseDocumentationCustomizer() {
    return openApi -> {
      openApi.getPaths().forEach(
          (path, pathItem) -> pathItem.readOperationsMap().forEach(
              (method, operation) -> {
                ApiResponses responses = operation.getResponses();
                if (responses == null) {
                  responses = new ApiResponses();
                  operation.setResponses(responses);
                }
                addError(responses, "500", "InternalServerError");
                if (!path.startsWith("/api/v1/auth/")) {
                  addError(responses, "401", "AuthenticationRequired");
                  addError(responses, "403", "AccessDenied");
                  operation.setDescription(
                      append(
                          operation.getDescription(),
                          "Authentication failures: `AUTHENTICATION_REQUIRED` (no token), "
                              + "`ACCESS_TOKEN_EXPIRED` (refresh then retry once), or "
                              + "`ACCESS_TOKEN_INVALID` (do not refresh)."));
                }
                documentPath(path, method.name(), responses, operation);
              }));
    };
  }

  private void documentPath(
      String path, String method, ApiResponses responses, io.swagger.v3.oas.models.Operation operation) {
    if (path.equals("/api/v1/auth/register")) {
      addError(responses, "400", "ValidationFailed");
      addError(responses, "409", "EmailAlreadyExists");
    } else if (path.equals("/api/v1/auth/login")) {
      addError(responses, "400", "ValidationFailed");
      addError(responses, "401", "InvalidCredentials");
    } else if (path.equals("/api/v1/auth/refresh")) {
      addError(responses, "400", "ValidationFailed");
      addError(responses, "401", "InvalidRefreshToken");
    } else if (path.equals("/api/v1/auth/logout")) {
      addError(responses, "400", "ValidationFailed");
    } else if (path.startsWith("/api/v1/projects/{projectId}/documents")) {
      documentDocumentOperation(method, path, responses, operation);
    } else if (path.startsWith("/api/v1/projects")) {
      documentProjectOperation(method, path, responses, operation);
    } else if (path.startsWith("/api/v1/users")) {
      documentUserOperation(path, responses);
    }
  }

  private void documentProjectOperation(
      String method, String path, ApiResponses responses, io.swagger.v3.oas.models.Operation operation) {
    if ("POST".equals(method) || "PATCH".equals(method))
      addError(responses, "400", "ValidationFailed");
    if (!path.equals("/api/v1/projects"))
      addError(responses, "404", "ProjectNotFound");
    if ("GET".equals(method) && path.equals("/api/v1/projects")) {
      operation.setDescription(
          append(
              operation.getDescription(),
              "Pagination: `page` is zero-based (default `0`), `size` is `1..100` (default `20`). "
                  + "Sort using `sortBy`: `createdAt`, `updatedAt`, or `title`; `direction`: `asc` or `desc`. "
                  + "`createdFrom` and `createdTo` are ISO-8601 timestamps."));
      addError(responses, "400", "InvalidParameter");
    }
  }

  private void documentDocumentOperation(
      String method, String path, ApiResponses responses, io.swagger.v3.oas.models.Operation operation) {
    addError(responses, "404", "ProjectNotFound");
    if (!path.equals("/api/v1/projects/{projectId}/documents"))
      addError(responses, "404", "DocumentNotFound");
    if ("POST".equals(method)) {
      addError(responses, "400", "InvalidParameter");
      addError(responses, "500", "StorageError");
      operation.setDescription(
          append(
              operation.getDescription(),
              "Upload uses `multipart/form-data`: `file` is required and `title` is optional. Allowed extensions: "
                  + "PDF, DOCX, DOC, XLSX, XLS, PPTX, PPT, Markdown, TXT. The resolved title is limited to 255 characters."));
    }
    if ("PATCH".equals(method))
      addError(responses, "400", "ValidationFailed");
    if ("GET".equals(method) && path.equals("/api/v1/projects/{projectId}/documents")) {
      addError(responses, "400", "InvalidParameter");
      operation.setDescription(
          append(
              operation.getDescription(),
              "Pagination: zero-based `page` (default `0`), `size` from `1` to `100` (default `20`), "
                  + "`sortBy` is `createdAt`, `updatedAt`, or `title`, and `direction` is `asc` or `desc`."));
    }
  }

  private void documentUserOperation(String path, ApiResponses responses) {
    if (!path.equals("/api/v1/users/me"))
      addError(responses, "400", "ValidationFailed");
    if (path.equals("/api/v1/users/me/email"))
      addError(responses, "409", "EmailAlreadyExists");
  }

  private void addError(ApiResponses responses, String status, String component) {
    responses.putIfAbsent(status, new ApiResponse().$ref("#/components/responses/" + component));
  }

  private String append(String original, String addition) {
    return original == null || original.isBlank() ? addition : original + "\n\n" + addition;
  }

  private Map<String, ApiResponse> errorResponses() {
    Map<String, ApiResponse> responses = new LinkedHashMap<>();
    responses.put("ValidationFailed", errorResponse("400", "Request validation failed", "VALIDATION_FAILED",
        "must not be blank", Map.of("email", "must be a well-formed email address", "password", "must not be blank")));
    responses.put("MalformedRequest", errorResponse("400", "Malformed or missing JSON request body",
        "MALFORMED_REQUEST", "Request body is missing or malformed", Map.of()));
    responses.put("InvalidParameter", errorResponse("400", "Invalid query, path, or upload parameter",
        "INVALID_PARAMETER", "page must be non-negative and size must be between 1 and 100", Map.of()));
    responses.put("AuthenticationRequired", errorResponse("401", "Authentication is required; do not refresh",
        "AUTHENTICATION_REQUIRED", "Authentication is required", Map.of()));
    responses.put("AccessTokenExpired", errorResponse("401", "Access token expired; refresh then retry once",
        "ACCESS_TOKEN_EXPIRED", "Access token has expired", Map.of()));
    responses.put("AccessTokenInvalid", errorResponse("401", "Invalid or malformed access token; do not refresh",
        "ACCESS_TOKEN_INVALID", "Access token is invalid", Map.of()));
    responses.put("InvalidCredentials", errorResponse("401", "Login credentials are invalid", "INVALID_CREDENTIALS",
        "Invalid email or password", Map.of()));
    responses.put("InvalidRefreshToken", errorResponse("401", "Refresh token is invalid or expired",
        "INVALID_REFRESH_TOKEN", "Invalid or expired refresh token", Map.of()));
    responses.put("AccessDenied", errorResponse("403", "Authenticated user lacks the required permission",
        "ACCESS_DENIED", "You do not have permission to access this resource", Map.of()));
    responses.put("EmailAlreadyExists", errorResponse("409", "An account already uses this email address",
        "EMAIL_ALREADY_EXISTS", "An account already exists for this email", Map.of()));
    responses.put("ProjectNotFound",
        errorResponse("404", "Project does not exist or does not belong to the current user", "PROJECT_NOT_FOUND",
            "Project not found", Map.of()));
    responses.put("DocumentNotFound", errorResponse("404", "Document does not exist in this project",
        "DOCUMENT_NOT_FOUND", "Document not found", Map.of()));
    responses.put("StorageError", errorResponse("500", "Document storage operation failed", "STORAGE_ERROR",
        "Could not load document", Map.of()));
    responses.put("InternalServerError", errorResponse("500", "Unexpected server error", "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred", Map.of()));
    return responses;
  }

  private ApiResponse errorResponse(
      String status, String description, String code, String message, Map<String, String> fieldErrors) {
    return new ApiResponse()
        .description(description)
        .content(
            new Content()
                .addMediaType(
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                    new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/" + ERROR_SCHEMA))
                        .example(
                            Map.of(
                                "timestamp", "2026-09-25T07:00:00Z",
                                "status", Integer.parseInt(status),
                                "error",
                                org.springframework.http.HttpStatus.valueOf(Integer.parseInt(status)).getReasonPhrase(),
                                "code", code,
                                "message", message,
                                "fieldErrors", fieldErrors))));
  }
}
