package vn.edu.fsoftacademy.api.application.exception;

public class DocumentNotFoundException extends RuntimeException {
  public DocumentNotFoundException() {
    super("Document not found");
  }
}
