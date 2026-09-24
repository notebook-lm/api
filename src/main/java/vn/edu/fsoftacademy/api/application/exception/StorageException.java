package vn.edu.fsoftacademy.api.application.exception;

public class StorageException extends RuntimeException {
  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
