package vn.edu.fsoftacademy.api.application.port;

import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

public interface MessagePublisher {
  void publish(String topic, OutboxEvent event);
}
