package vn.edu.fsoftacademy.api.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
    String endpoint, String bucket, String accessKey, String secretKey) {}
