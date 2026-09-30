package vn.edu.fsoftacademy.api.infrastructure.ai;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="app.ai.gemini")
public record GeminiProperties(String apiKey, String model, String baseUrl, int timeoutSeconds) {}
