package com.franchise.project.r2dbc.resilience;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceResilienceTest {

    private static final PersistenceResilienceProperties PROPERTIES = new PersistenceResilienceProperties(
            "test", Duration.ofSeconds(2), 2, Duration.ofMillis(200), 16);

    @Test
    void readRetriesTransientErrorsWithBackoffUntilItSucceeds() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.read(failingTimes(2, attempts)))
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(90))
                .thenAwait(Duration.ofSeconds(5))
                .expectNext("row")
                .verifyComplete();
        assertEquals(3, attempts.get());
    }

    @Test
    void readGivesUpAfterMaxRetriesAndPropagatesTheOriginalError() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.read(failingTimes(10, attempts)))
                .thenAwait(Duration.ofSeconds(10))
                .expectError(TransientDataAccessResourceException.class)
                .verify();
        assertEquals(3, attempts.get());
    }

    @Test
    void readDoesNotRetryNonTransientErrors() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);
        Mono<String> query = Mono.defer(() -> {
            attempts.incrementAndGet();
            return Mono.error(new DataIntegrityViolationException("duplicated"));
        });

        StepVerifier.create(resilience.read(query))
                .expectError(DataIntegrityViolationException.class)
                .verify();
        assertEquals(1, attempts.get());
    }

    @Test
    void writeIsNeverRetriedBecauseItIsNotIdempotent() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.create(resilience.write(failingTimes(1, attempts)))
                .expectError(TransientDataAccessResourceException.class)
                .verify();
        assertEquals(1, attempts.get());
    }

    @Test
    void writeFailsWithTimeoutWhenTheDatabaseDoesNotAnswer() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.write(Mono.never()))
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(1900))
                .thenAwait(Duration.ofMillis(100))
                .expectError(TimeoutException.class)
                .verify();
    }

    @Test
    void circuitOpensAfterFailureThresholdAndRejectsCallsWithoutHittingTheDatabase() {
        CircuitBreaker circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom()
                .slidingWindowSize(4)
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build());
        PersistenceResilience resilience = new PersistenceResilience(circuitBreaker, PROPERTIES);
        AtomicInteger attempts = new AtomicInteger();

        StepVerifier.create(Flux.range(0, 4)
                        .concatMap(call -> resilience.write(failingTimes(Integer.MAX_VALUE, attempts))
                                .onErrorResume(TransientDataAccessResourceException.class, error -> Mono.empty())))
                .verifyComplete();

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        StepVerifier.create(resilience.write(failingTimes(Integer.MAX_VALUE, attempts)))
                .expectError(CallNotPermittedException.class)
                .verify();
        assertEquals(4, attempts.get());
    }

    @Test
    void readOfFluxStreamsEveryElement() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.create(resilience.read(Flux.range(1, 100)))
                .expectNextCount(100)
                .verifyComplete();
    }

    private static Mono<String> failingTimes(int failures, AtomicInteger attempts) {
        return Mono.fromCallable(attempts::incrementAndGet)
                .filter(attempt -> attempt > failures)
                .map(attempt -> "row")
                .switchIfEmpty(Mono.error(() -> new TransientDataAccessResourceException("connection reset")));
    }
}
