package vn.edu.fsoftacademy.api.infrastructure.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.AiProviderException;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageRole;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageStatus;

class OpenAiChatProviderTest {
  @Test
  void streamsOpenAiContentDeltas() throws Exception {
    AtomicReference<String> requestBody = new AtomicReference<>();
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/v1/chat/completions", exchange -> {
      requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      byte[] body = ("data: {\"choices\":[{\"delta\":{\"content\":\"Hello\"}}]}\n\n"
          + "data: {\"choices\":[{\"delta\":{\"content\":\" world\"}}]}\n\n"
          + "data: [DONE]\n\n").getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
      exchange.sendResponseHeaders(200, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.start();
    try {
      var provider = new OpenAiChatProvider(
          new OpenAiProperties("test-key", "test-model", "http://localhost:" + server.getAddress().getPort(), 10),
          new ObjectMapper());
      var output = new StringBuilder();
      var message = new ChatMessage(
          java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), ChatMessageRole.USER, "Question",
          ChatMessageStatus.COMPLETED, null, Instant.now(), Instant.now());

      provider.stream(List.of(message), output::append, () -> false);

      assertEquals("Hello world", output.toString());
      assertEquals("openai", provider.name());
      org.junit.jupiter.api.Assertions.assertTrue(requestBody.get().contains("\"stream\":true"));
      org.junit.jupiter.api.Assertions.assertTrue(requestBody.get().contains("\"model\":\"test-model\""));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void rejectsMissingApiKey() {
    var provider = new OpenAiChatProvider(
        new OpenAiProperties("", "test-model", "https://api.openai.com", 10), new ObjectMapper());

    assertThrows(AiProviderException.class, () -> provider.stream(List.of(), ignored -> {}, () -> false));
  }
}
