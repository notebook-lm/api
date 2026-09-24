package vn.edu.fsoftacademy.api.application.port;

import java.io.InputStream;

public interface ObjectStorage {
  void put(String key, InputStream content, long size, String contentType);

  InputStream get(String key);

  void delete(String key);
}
