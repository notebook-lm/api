package vn.edu.fsoftacademy.api.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.port.JsonMapper;

@Component
public class JacksonJsonMapper implements JsonMapper {
  private final ObjectMapper objectMapper;

  public JacksonJsonMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String write(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Could not serialize JSON", ex);
    }
  }

  @Override
  public <T> T read(String json, Class<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Could not deserialize JSON", ex);
    }
  }
}
