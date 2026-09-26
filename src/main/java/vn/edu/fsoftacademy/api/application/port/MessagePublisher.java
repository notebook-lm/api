package vn.edu.fsoftacademy.api.application.port;

import vn.edu.fsoftacademy.api.domain.entity.OutboxEvent;

public interface MessagePublisher {
  void publish(OutboxEvent event);
}
