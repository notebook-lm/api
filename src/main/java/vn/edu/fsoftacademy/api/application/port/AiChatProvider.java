package vn.edu.fsoftacademy.api.application.port;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;

public interface AiChatProvider {
  void stream(String systemInstruction, List<ChatMessage> history, Consumer<String> onDelta, BooleanSupplier isCancelled);

  default void stream(List<ChatMessage> history, Consumer<String> onDelta, BooleanSupplier isCancelled) {
    stream("", history, onDelta, isCancelled);
  }

  String name();
}
