package vn.edu.fsoftacademy.api.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
  public static final String BEARER_AUTH = "bearerAuth";

  @Bean
  OpenAPI notebookLmOpenApi(@Value("${server.port:8080}") int port) {
    return new OpenAPI()
        .info(new Info()
            .title("Notebook LM API")
            .version("v1")
            .description("REST API for account authentication and user profile management.")
            .license(new License().name("Private")))
        .addServersItem(new Server().url("http://localhost:" + port).description("Local development"))
        .components(new Components().addSecuritySchemes(BEARER_AUTH,
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste the access token returned by `/api/v1/auth/login`.")))
        .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
  }
}
