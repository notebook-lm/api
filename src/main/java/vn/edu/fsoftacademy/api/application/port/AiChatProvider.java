package vn.edu.fsoftacademy.api.application.port;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import vn.edu.fsoftacademy.api.domain.entity.ChatMessage;

public interface AiChatProvider {
    void stream(List<ChatMessage> history, Consumer<String> onDelta, BooleanSupplier isCancelled);

    String name();
}
