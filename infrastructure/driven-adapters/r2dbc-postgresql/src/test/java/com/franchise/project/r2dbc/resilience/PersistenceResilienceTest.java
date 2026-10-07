package com.franchise.project.r2dbc.resilience;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.exception.TechnicalException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessResourceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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
    void readGivesUpAfterMaxRetriesAndReportsTheServiceAsUnavailable() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.read(failingTimes(10, attempts)))
                .thenAwait(Duration.ofSeconds(10))
                .expectErrorMatches(unavailableCausedBy(TransientDataAccessResourceException.class))
                .verify();
        assertEquals(3, attempts.get());
    }

    @Test
    void readDoesNotRetryNonTransientErrorsAndLetsThemThrough() {
        AtomicInteger attempts = new AtomicInteger();
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);
        Mono<String> query = Mono.defer(() -> {
            attempts.incrementAndGet();
            return Mono.error(new DataIntegrityViolationException("broken foreign key"));
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
                .expectErrorMatches(unavailableCausedBy(TransientDataAccessResourceException.class))
                .verify();
        assertEquals(1, attempts.get());
    }

    @Test
    void writeReportsTheServiceAsUnavailableWhenTheDatabaseDoesNotAnswer() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.write(Mono.never()))
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(1900))
                .thenAwait(Duration.ofMillis(100))
                .expectErrorMatches(unavailableCausedBy(TimeoutException.class))
                .verify();
    }

    @Test
    void writeTurnsADuplicatedKeyIntoABusinessError() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.create(resilience.write(Mono.error(new DuplicateKeyException("uq_product_branch_name"))))
                .expectErrorMatches(error -> error instanceof BusinessException business
                        && business.getTechnicalMessage() == TechnicalMessage.RESOURCE_ALREADY_EXISTS)
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
                                .onErrorResume(TechnicalException.class, error -> Mono.empty())))
                .verifyComplete();

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        StepVerifier.create(resilience.write(failingTimes(Integer.MAX_VALUE, attempts)))
                .expectErrorMatches(unavailableCausedBy(CallNotPermittedException.class))
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

    @Test
    void readOfFluxReportsTheServiceAsUnavailableWhenTheConnectionFails() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.withVirtualTime(() -> resilience.read(
                        Flux.<Integer>error(new DataAccessResourceFailureException("Failed to obtain R2DBC Connection"))))
                .thenAwait(Duration.ofSeconds(10))
                .expectErrorMatches(unavailableCausedBy(DataAccessResourceFailureException.class))
                .verify();
    }

    @Test
    void readOfFluxTurnsADuplicatedKeyIntoABusinessError() {
        PersistenceResilience resilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"), PROPERTIES);

        StepVerifier.create(resilience.read(Flux.<Integer>error(new DuplicateKeyException("uq_branch_name"))))
                .expectError(BusinessException.class)
                .verify();
    }

    private static Predicate<Throwable> unavailableCausedBy(Class<? extends Throwable> cause) {
        return error -> error instanceof TechnicalException technical
                && technical.getTechnicalMessage() == TechnicalMessage.SERVICE_UNAVAILABLE
                && cause.isInstance(technical.getCause());
    }

    private static Mono<String> failingTimes(int failures, AtomicInteger attempts) {
        return Mono.fromCallable(attempts::incrementAndGet)
                .filter(attempt -> attempt > failures)
                .map(attempt -> "row")
                .switchIfEmpty(Mono.error(() -> new TransientDataAccessResourceException("connection reset")));
    }
}
