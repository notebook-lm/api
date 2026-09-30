package vn.edu.fsoftacademy.api.api.rest.shared.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class FrameworkExceptionHandlerTest {
  private final FrameworkExceptionHandler handler = new FrameworkExceptionHandler();

  @Test
  void mapsUnexpectedExceptionsToSafeInternalServerError() {
    var response = handler.unexpected(new RuntimeException("database password leaked"));

    assertEquals(500, response.getStatusCode().value());
    assertEquals("An unexpected error occurred", response.getBody().message());
    assertEquals(Map.of(), response.getBody().fieldErrors());
  }

  @Test
  void usesStatusReasonWhenExceptionMessageIsBlank() {
    var response = handler.missingRequestValue(new IllegalArgumentException(""));

    assertEquals(400, response.getStatusCode().value());
    assertEquals("Bad Request", response.getBody().message());
    assertTrue(response.getBody().timestamp().isBefore(java.time.Instant.now().plusSeconds(1)));
  }
}
