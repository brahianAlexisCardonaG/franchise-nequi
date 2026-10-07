package com.franchise.project.r2dbc.resilience;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "persistence.resilience")
public record PersistenceResilienceProperties(
        String circuitBreakerName,
        Duration timeout,
        long maxRetries,
        Duration retryMinBackoff,
        int readPrefetch
) {
}
