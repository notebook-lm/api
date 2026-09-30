package vn.edu.fsoftacademy.api.application.exception;

public class DocumentContentNotAvailableException extends RuntimeException {
  public DocumentContentNotAvailableException() {
    super("Extracted document content is not available.");
  }
}
