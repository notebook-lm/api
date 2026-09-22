package vn.edu.fsoftacademy.api.domain.entity;

import java.util.UUID;

public record Permission(UUID id, String code) {
  public Permission(String code) {
    this(UUID.randomUUID(), code);
  }
}
