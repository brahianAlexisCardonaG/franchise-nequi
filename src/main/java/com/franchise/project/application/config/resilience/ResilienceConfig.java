package com.franchise.project.application.config.resilience;

import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilienceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PersistenceResilienceProperties.class)
public class ResilienceConfig {

    @Bean
    public PersistenceResilience persistenceResilience(CircuitBreakerRegistry circuitBreakerRegistry,
                                                       PersistenceResilienceProperties properties) {
        return new PersistenceResilience(
                circuitBreakerRegistry.circuitBreaker(properties.circuitBreakerName()),
                properties);
    }
}
