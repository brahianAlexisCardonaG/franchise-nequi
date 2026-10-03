package com.franchise.project.infrastructure.adapters.persistenceadapter.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class PersistenceResilience {

    private final CircuitBreaker circuitBreaker;
    private final PersistenceResilienceProperties properties;

    public <T> Mono<T> read(Mono<T> query) {
        return write(query)
                .retryWhen(transientErrorRetry());
    }

    public <T> Flux<T> read(Flux<T> query) {
        return query
                .limitRate(properties.readPrefetch())
                .timeout(properties.timeout())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .retryWhen(transientErrorRetry());
    }

    public <T> Mono<T> write(Mono<T> command) {
        return command
                .timeout(properties.timeout())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private Retry transientErrorRetry() {
        return Retry.backoff(properties.maxRetries(), properties.retryMinBackoff())
                .filter(PersistenceResilience::isTransient)
                .onRetryExhaustedThrow((retrySpec, signal) -> signal.failure());
    }

    private static boolean isTransient(Throwable error) {
        return error instanceof TransientDataAccessException
                || error instanceof DataAccessResourceFailureException
                || error instanceof TimeoutException;
    }
}
