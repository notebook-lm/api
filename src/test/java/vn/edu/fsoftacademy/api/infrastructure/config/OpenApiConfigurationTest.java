package vn.edu.fsoftacademy.api.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.Test;

class OpenApiConfigurationTest {
  private final OpenApiConfiguration configuration = new OpenApiConfiguration();

  @Test
  void documentsReusableErrorResponsesAndBearerAuthentication() {
    OpenAPI openApi = configuration.notebookLmOpenApi(8080);

    assertNotNull(openApi.getComponents().getSecuritySchemes().get(OpenApiConfiguration.BEARER_AUTH));
    assertNotNull(openApi.getComponents().getResponses().get("ValidationFailed"));
    assertNotNull(openApi.getComponents().getResponses().get("AccessTokenExpired"));
    assertNotNull(openApi.getComponents().getResponses().get("AccessTokenInvalid"));
    assertNotNull(openApi.getComponents().getResponses().get("EmailAlreadyExists"));
    assertTrue(openApi.getInfo().getDescription().contains("ACCESS_TOKEN_EXPIRED"));
  }

  @Test
  void addsRelevantErrorResponsesToProtectedOperations() {
    OpenAPI openApi = configuration.notebookLmOpenApi(8080);
    Operation operation = new Operation();
    operation.setDescription("List my projects");
    PathItem pathItem = new PathItem().get(operation);
    openApi.setPaths(new Paths().addPathItem("/api/v1/projects", pathItem));

    configuration.errorResponseDocumentationCustomizer().customise(openApi);

    assertEquals(
        "#/components/responses/AuthenticationRequired",
        operation.getResponses().get("401").get$ref());
    assertEquals(
        "#/components/responses/AccessDenied", operation.getResponses().get("403").get$ref());
    assertEquals(
        "#/components/responses/InvalidParameter", operation.getResponses().get("400").get$ref());
    assertEquals(
        "#/components/responses/InternalServerError", operation.getResponses().get("500").get$ref());
  }
}
