package vn.edu.fsoftacademy.api.application.exception;

public class ProjectNotFoundException extends RuntimeException {
  public ProjectNotFoundException() {
    super("Project not found");
  }
}
