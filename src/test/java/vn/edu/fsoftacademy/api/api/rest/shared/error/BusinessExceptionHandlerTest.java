package vn.edu.fsoftacademy.api.api.rest.shared.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.ConflictException;
import vn.edu.fsoftacademy.api.application.exception.ProjectNotFoundException;

class BusinessExceptionHandlerTest {
  private final BusinessExceptionHandler handler = new BusinessExceptionHandler();

  @Test
  void mapsConflictToConflictResponse() {
    var response = handler.conflict(new ConflictException("Email already used"));

    assertEquals(409, response.getStatusCode().value());
    assertEquals("Email already used", response.getBody().message());
  }
  @Test
  void mapsProjectNotFoundToNotFoundResponse() {
    var response = handler.projectNotFound(new ProjectNotFoundException());

    assertEquals(404, response.getStatusCode().value());
    assertEquals("Project not found", response.getBody().message());
  }
}
