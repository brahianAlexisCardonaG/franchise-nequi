package com.franchise.project.r2dbc.resilience;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.exception.TechnicalException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
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
        return protect(query)
                .retryWhen(transientErrorRetry())
                .transform(PersistenceResilience::translateErrors);
    }

    public <T> Flux<T> read(Flux<T> query) {
        return query
                .limitRate(properties.readPrefetch())
                .timeout(properties.timeout())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .retryWhen(transientErrorRetry())
                .onErrorMap(DuplicateKeyException.class, PersistenceResilience::alreadyExists)
                .onErrorMap(PersistenceResilience::isUnavailable, PersistenceResilience::unavailable);
    }

    public <T> Mono<T> write(Mono<T> command) {
        return protect(command)
                .transform(PersistenceResilience::translateErrors);
    }

    private <T> Mono<T> protect(Mono<T> operation) {
        return operation
                .timeout(properties.timeout())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private static <T> Mono<T> translateErrors(Mono<T> operation) {
        return operation
                .onErrorMap(DuplicateKeyException.class, PersistenceResilience::alreadyExists)
                .onErrorMap(PersistenceResilience::isUnavailable, PersistenceResilience::unavailable);
    }

    private Retry transientErrorRetry() {
        return Retry.backoff(properties.maxRetries(), properties.retryMinBackoff())
                .filter(PersistenceResilience::isTransient)
                .onRetryExhaustedThrow((retrySpec, signal) -> signal.failure());
    }

    private static Throwable alreadyExists(DuplicateKeyException error) {
        return new BusinessException(TechnicalMessage.RESOURCE_ALREADY_EXISTS);
    }

    private static Throwable unavailable(Throwable error) {
        return new TechnicalException(TechnicalMessage.SERVICE_UNAVAILABLE, error);
    }

    private static boolean isUnavailable(Throwable error) {
        return isTransient(error) || error instanceof CallNotPermittedException;
    }

    private static boolean isTransient(Throwable error) {
        return error instanceof TransientDataAccessException
                || error instanceof DataAccessResourceFailureException
                || error instanceof TimeoutException;
    }
}
