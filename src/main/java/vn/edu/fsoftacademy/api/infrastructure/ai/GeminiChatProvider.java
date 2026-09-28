package vn.edu.fsoftacademy.api.infrastructure.ai;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import vn.edu.fsoftacademy.api.application.exception.AiProviderException;
import vn.edu.fsoftacademy.api.application.port.AiChatProvider;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessageRole;

public class GeminiChatProvider implements AiChatProvider {
 private final GeminiProperties properties; private final ObjectMapper json; private final HttpClient client;
 public GeminiChatProvider(GeminiProperties properties,ObjectMapper json){this.properties=properties;this.json=json;this.client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(properties.timeoutSeconds())).build();}
 public String name(){return "gemini";}
 public void stream(List<ChatMessage> history, Consumer<String> onDelta) {
  if(properties.apiKey()==null||properties.apiKey().isBlank()) throw new AiProviderException("Gemini is not configured. Set GEMINI_API_KEY.");
  try {
   List<Map<String,Object>> contents=new ArrayList<>();
   for(ChatMessage m:history) if(m.getStatus().name().equals("COMPLETED")) contents.add(Map.of("role",m.getRole()==ChatMessageRole.ASSISTANT?"model":"user","parts",List.of(Map.of("text",m.getContent()))));
   String body=json.writeValueAsString(Map.of("contents",contents,"generationConfig",Map.of("temperature",0.7)));
   String endpoint=properties.baseUrl().replaceAll("/$","")+"/v1beta/models/"+properties.model()+":streamGenerateContent?alt=sse";
   HttpRequest request=HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofSeconds(properties.timeoutSeconds())).header("Content-Type","application/json").header("x-goog-api-key",properties.apiKey()).POST(HttpRequest.BodyPublishers.ofString(body)).build();
   HttpResponse<java.util.stream.Stream<String>> response=client.send(request,HttpResponse.BodyHandlers.ofLines());
   if(response.statusCode()<200||response.statusCode()>=300){response.body().close();throw new AiProviderException("Gemini generation request failed (HTTP "+response.statusCode()+").");}
   try(var lines=response.body()) { lines.filter(line->line.startsWith("data: ")).forEach(line->emit(line.substring(6),onDelta)); }
  } catch(AiProviderException e){throw e;} catch(Exception e){throw new AiProviderException("Gemini generation failed.",e);}
 }
 private void emit(String event,Consumer<String> onDelta){try{JsonNode root=json.readTree(event);JsonNode candidates=root.path("candidates");if(!candidates.isArray()||candidates.isEmpty())return;JsonNode parts=candidates.get(0).path("content").path("parts");if(parts.isArray())for(JsonNode part:parts){String text=part.path("text").asText("");if(!text.isEmpty())onDelta.accept(text);}}catch(Exception e){throw new AiProviderException("Could not parse Gemini stream.",e);}}
}
