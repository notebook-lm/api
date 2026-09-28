package vn.edu.fsoftacademy.api.application.port;

public interface JsonMapper {
  String write(Object value);

  <T> T read(String json, Class<T> type);
}
