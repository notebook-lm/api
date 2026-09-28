package vn.edu.fsoftacademy.api.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.infrastructure.ai.AiProperties;
import vn.edu.fsoftacademy.api.infrastructure.ai.GeminiChatProvider;
import vn.edu.fsoftacademy.api.infrastructure.ai.GeminiProperties;
import vn.edu.fsoftacademy.api.infrastructure.ai.OpenAiChatProvider;
import vn.edu.fsoftacademy.api.infrastructure.ai.OpenAiProperties;

class ApplicationWiringConfigTest {
  private final ApplicationWiringConfig config = new ApplicationWiringConfig();
  private final GeminiProperties gemini = new GeminiProperties("key", "gemini-model", "https://example.test", 10);
  private final OpenAiProperties openai = new OpenAiProperties("key", "openai-model", "https://example.test", 10);

  @Test
  void selectsGeminiByDefault() {
    assertInstanceOf(GeminiChatProvider.class, config.aiChatProvider(new AiProperties(null), gemini, openai, new ObjectMapper()));
  }

  @Test
  void selectsOpenAiIgnoringCaseAndWhitespace() {
    assertInstanceOf(OpenAiChatProvider.class, config.aiChatProvider(new AiProperties(" OpenAI "), gemini, openai, new ObjectMapper()));
  }

  @Test
  void rejectsUnsupportedProvider() {
    assertThrows(IllegalArgumentException.class,
        () -> config.aiChatProvider(new AiProperties("anthropic"), gemini, openai, new ObjectMapper()));
  }
}
