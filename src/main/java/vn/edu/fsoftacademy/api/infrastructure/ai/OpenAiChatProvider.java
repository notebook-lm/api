package vn.edu.fsoftacademy.api.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.edu.fsoftacademy.api.application.exception.AiProviderException;
import vn.edu.fsoftacademy.api.application.port.AiChatProvider;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageRole;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageStatus;

public class OpenAiChatProvider implements AiChatProvider {
  private static final Logger log = LoggerFactory.getLogger(OpenAiChatProvider.class);

  private final OpenAiProperties properties;
  private final ObjectMapper json;
  private final HttpClient client;

  public OpenAiChatProvider(OpenAiProperties properties, ObjectMapper json) {
    this.properties = properties;
    this.json = json;
    this.client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(properties.timeoutSeconds()))
        .build();
  }

  @Override
  public String name() {
    return "openai";
  }

  @Override
  public void stream(String systemInstruction, List<ChatMessage> history, Consumer<String> onDelta, BooleanSupplier isCancelled) {
    if (properties.apiKey() == null || properties.apiKey().isBlank()) {
      throw new AiProviderException("OpenAI is not configured. Set OPENAI_API_KEY.");
    }

    try {
      List<Map<String, String>> messages = new ArrayList<>();
      if (systemInstruction != null && !systemInstruction.isBlank()) {
        messages.add(Map.of("role", "system", "content", systemInstruction));
      }
      for (ChatMessage message : history) {
        if (message.getStatus() == ChatMessageStatus.COMPLETED) {
          messages.add(Map.of(
              "role", message.getRole() == ChatMessageRole.ASSISTANT ? "assistant" : "user",
              "content", message.getContent()));
        }
      }
      Map<String, Object> requestBody = new LinkedHashMap<>();
      requestBody.put("model", properties.model());
      requestBody.put("messages", messages);
      requestBody.put("temperature", 0.7);
      requestBody.put("stream", true);

      HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint()))
          .timeout(Duration.ofSeconds(properties.timeoutSeconds()))
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + properties.apiKey())
          .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(requestBody)))
          .build();
      HttpResponse<java.util.stream.Stream<String>> response =
          client.send(request, HttpResponse.BodyHandlers.ofLines());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        try (var body = response.body()) {
          String responseBody = body.reduce("", (result, line) -> result + line);
          log.warn("OpenAI generation request failed with HTTP {}: {}", response.statusCode(), responseBody);
        }
        throw new AiProviderException("OpenAI generation request failed (HTTP " + response.statusCode() + ").");
      }
      try (var lines = response.body()) {
        var iterator = lines.iterator();
        while (!isCancelled.getAsBoolean() && iterator.hasNext()) {
          String line = iterator.next();
          if (line.startsWith("data: ")) {
            emit(line.substring(6), onDelta);
          }
        }
      }
    } catch (AiProviderException e) {
      throw e;
    } catch (Exception e) {
      log.error("OpenAI generation failed for endpoint {} and model {}", endpoint(), properties.model(), e);
      throw new AiProviderException("OpenAI generation failed.", e);
    }
  }

  private String endpoint() {
    String baseUrl = properties.baseUrl().replaceAll("/$", "");
    return baseUrl.endsWith("/v1") ? baseUrl + "/chat/completions" : baseUrl + "/v1/chat/completions";
  }

  private void emit(String event, Consumer<String> onDelta) {
    if ("[DONE]".equals(event)) {
      return;
    }
    try {
      JsonNode root = json.readTree(event);
      JsonNode choices = root.path("choices");
      if (!choices.isArray() || choices.isEmpty()) {
        return;
      }
      String content = choices.get(0).path("delta").path("content").asText("");
      if (!content.isEmpty()) {
        onDelta.accept(content);
      }
    } catch (Exception e) {
      throw new AiProviderException("Could not parse OpenAI stream.", e);
    }
  }
}
