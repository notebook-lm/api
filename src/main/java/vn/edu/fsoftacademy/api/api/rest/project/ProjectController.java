package vn.edu.fsoftacademy.api.api.rest.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fsoftacademy.api.api.rest.project.dto.request.CreateProjectRequest;
import vn.edu.fsoftacademy.api.api.rest.project.dto.request.UpdateProjectRequest;
import vn.edu.fsoftacademy.api.api.rest.project.dto.response.ProjectResponse;
import vn.edu.fsoftacademy.api.application.command.createproject.CreateProjectCommand;
import vn.edu.fsoftacademy.api.application.command.createproject.CreateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.createproject.CreateProjectResult;
import vn.edu.fsoftacademy.api.application.command.deleteproject.DeleteProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateproject.UpdateProjectCommand;
import vn.edu.fsoftacademy.api.application.command.updateproject.UpdateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateproject.UpdateProjectResult;
import vn.edu.fsoftacademy.api.application.query.getproject.GetProjectQueryHandler;
import vn.edu.fsoftacademy.api.application.query.getproject.GetProjectResult;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsResult;

@RestController
@Tag(name = "Projects", description = "JWT-protected operations for private projects.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/projects")
public class ProjectController {
  private final CreateProjectCommandHandler create;
  private final ListProjectsQueryHandler list;
  private final GetProjectQueryHandler get;
  private final UpdateProjectCommandHandler update;
  private final DeleteProjectCommandHandler delete;

  public ProjectController(CreateProjectCommandHandler create, ListProjectsQueryHandler list,
      GetProjectQueryHandler get, UpdateProjectCommandHandler update, DeleteProjectCommandHandler delete) {
    this.create = create; this.list = list; this.get = get; this.update = update; this.delete = delete;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a project", description = "Requires `project:create`.")
  @PreAuthorize("hasAuthority('project:create')")
  public ProjectResponse create(@AuthenticationPrincipal UUID ownerId, @Valid @RequestBody CreateProjectRequest request) {
    return response(create.execute(ownerId, new CreateProjectCommand(request.title(), request.description())));
  }

  @GetMapping
  @Operation(summary = "List my projects", description = "Requires `project:read`.")
  @PreAuthorize("hasAuthority('project:read')")
  public List<ProjectResponse> list(@AuthenticationPrincipal UUID ownerId) {
    return list.handle(ownerId).stream().map(this::response).toList();
  }

  @GetMapping("/{projectId}")
  @Operation(summary = "Get my project", description = "Requires `project:read`.")
  @PreAuthorize("hasAuthority('project:read')")
  public ProjectResponse get(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId) {
    return response(get.handle(ownerId, projectId));
  }

  @PatchMapping("/{projectId}")
  @Operation(summary = "Update my project", description = "Requires `project:update`.")
  @PreAuthorize("hasAuthority('project:update')")
  public ProjectResponse update(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId,
      @Valid @RequestBody UpdateProjectRequest request) {
    return response(update.execute(ownerId, projectId, new UpdateProjectCommand(request.title(), request.description())));
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete my project", description = "Requires `project:delete`.")
  @PreAuthorize("hasAuthority('project:delete')")
  public void delete(@AuthenticationPrincipal UUID ownerId, @PathVariable UUID projectId) {
    delete.execute(ownerId, projectId);
  }

  private ProjectResponse response(CreateProjectResult result) { return new ProjectResponse(result.id(), result.title(), result.description(), result.createdAt(), result.updatedAt()); }
  private ProjectResponse response(ListProjectsResult result) { return new ProjectResponse(result.id(), result.title(), result.description(), result.createdAt(), result.updatedAt()); }
  private ProjectResponse response(GetProjectResult result) { return new ProjectResponse(result.id(), result.title(), result.description(), result.createdAt(), result.updatedAt()); }
  private ProjectResponse response(UpdateProjectResult result) { return new ProjectResponse(result.id(), result.title(), result.description(), result.createdAt(), result.updatedAt()); }
}
