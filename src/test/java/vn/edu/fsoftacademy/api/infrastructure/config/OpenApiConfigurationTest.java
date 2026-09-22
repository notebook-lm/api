package vn.edu.fsoftacademy.api.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OpenApiConfigurationTest {
  @Test
  void publishesNotebookApiMetadataAndJwtBearerScheme() {
    var openApi = new OpenApiConfiguration().notebookLmOpenApi(8080);

    assertEquals("Notebook LM API", openApi.getInfo().getTitle());
    assertEquals("http://localhost:8080", openApi.getServers().getFirst().getUrl());
    assertEquals("bearer", openApi.getComponents().getSecuritySchemes()
        .get(OpenApiConfiguration.BEARER_AUTH).getScheme());
    assertTrue(openApi.getSecurity().stream().anyMatch(requirement ->
        requirement.containsKey(OpenApiConfiguration.BEARER_AUTH)));
  }
}
