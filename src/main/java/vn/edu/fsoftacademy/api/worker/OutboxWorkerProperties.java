package vn.edu.fsoftacademy.api.worker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxWorkerProperties(
    boolean enabled,
    long pollDelayMs,
    int batchSize,
    int maxAttempts,
    long initialBackoffSeconds,
    long maxBackoffSeconds,
    long claimLeaseSeconds,
    String topic) {}
