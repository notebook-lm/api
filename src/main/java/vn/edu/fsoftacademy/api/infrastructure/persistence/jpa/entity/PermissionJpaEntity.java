package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "permissions")
public class PermissionJpaEntity {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  protected PermissionJpaEntity() {}

  public PermissionJpaEntity(UUID id, String code) {
    this.id = id;
    this.code = code;
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }
}
