package vn.edu.fsoftacademy.api.api.rest.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.edu.fsoftacademy.api.api.rest.project.dto.request.CreateProjectRequest;
import vn.edu.fsoftacademy.api.api.rest.project.dto.request.UpdateProjectRequest;
import vn.edu.fsoftacademy.api.api.rest.shared.error.BusinessExceptionHandler;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ValidationExceptionHandler;
import vn.edu.fsoftacademy.api.application.command.createproject.*;
import vn.edu.fsoftacademy.api.application.command.deleteproject.DeleteProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateproject.*;
import vn.edu.fsoftacademy.api.application.query.getproject.*;
import vn.edu.fsoftacademy.api.application.query.listprojects.*;

class ProjectControllerTest {
  private final UUID ownerId = UUID.randomUUID();
  private final UUID projectId = UUID.randomUUID();
  private CreateProjectCommandHandler create;
  private UpdateProjectCommandHandler update;
  private DeleteProjectCommandHandler delete;
  private ProjectController controller;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    create = mock(CreateProjectCommandHandler.class);
    var list = mock(ListProjectsQueryHandler.class);
    var get = mock(GetProjectQueryHandler.class);
    update = mock(UpdateProjectCommandHandler.class);
    delete = mock(DeleteProjectCommandHandler.class);
    controller = new ProjectController(create, list, get, update, delete);
    mvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new BusinessExceptionHandler(), new ValidationExceptionHandler())
            .build();
  }

  @Test
  void createMapsRequestToOwnerScopedCommand() {
    Instant now = Instant.now();
    when(create.execute(any(), any()))
        .thenReturn(new CreateProjectResult(projectId, "Notes", null, now, now));

    var response = controller.create(ownerId, new CreateProjectRequest("Notes", null));

    assertEquals(projectId, response.id());
    verify(create).execute(ownerId, new CreateProjectCommand("Notes", null));
  }

  @Test
  void updateAndDeleteUseAuthenticatedOwner() {
    Instant now = Instant.now();
    when(update.execute(any(), any(), any()))
        .thenReturn(new UpdateProjectResult(projectId, "New", "Details", now, now));

    var response =
        controller.update(ownerId, projectId, new UpdateProjectRequest("New", "Details"));
    controller.delete(ownerId, projectId);

    assertEquals("New", response.title());
    verify(update).execute(ownerId, projectId, new UpdateProjectCommand("New", "Details"));
    verify(delete).execute(ownerId, projectId);
  }

  @Test
  void listMapsQueryParametersToPaginatedResponse() {
    var list = mock(ListProjectsQueryHandler.class);
    var get = mock(GetProjectQueryHandler.class);
    controller = new ProjectController(create, list, get, update, delete);
    Instant now = Instant.now();
    when(list.handle(eq(ownerId), any()))
        .thenReturn(
            new ProjectPage(
                List.of(new ListProjectsResult(projectId, "Notes", null, now, now)),
                1,
                10,
                11,
                2,
                false,
                true));

    var response = controller.list(ownerId, " notes ", null, null, "updatedAt", "asc", 1, 10);

    assertEquals(11, response.totalItems());
    assertEquals("Notes", response.items().getFirst().title());
    verify(list)
        .handle(
            eq(ownerId),
            eq(
                new ListProjectsQuery(
                    "notes", null, null, ProjectSortField.UPDATED_AT, SortDirection.ASC, 1, 10)));
  }

  @Test
  void listRejectsInvalidSortAndPageInputs() {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> controller.list(ownerId, null, null, null, "unknown", "desc", 0, 20));
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> controller.list(ownerId, null, null, null, "title", "desc", -1, 20));
  }

  @Test
  void createRejectsBlankAndOversizedFields() throws Exception {
    mvc.perform(
            post("/api/v1/projects")
                .principal(ownerId::toString)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"description\":\"ok\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));

    mvc.perform(
            post("/api/v1/projects")
                .principal(ownerId::toString)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\":\"Project\",\"description\":\"%s\"}".formatted("x".repeat(2001))))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(create);
  }
}
