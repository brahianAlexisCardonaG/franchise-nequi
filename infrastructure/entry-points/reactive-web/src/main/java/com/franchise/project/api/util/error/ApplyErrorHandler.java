package com.franchise.project.api.util.error;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyErrorHandler {

    private static final Map<TechnicalMessage, HttpStatus> STATUS_BY_MESSAGE = Map.of(
            TechnicalMessage.FRANCHISE_NOT_EXISTS, HttpStatus.NOT_FOUND,
            TechnicalMessage.BRANCH_NOT_EXISTS, HttpStatus.NOT_FOUND,
            TechnicalMessage.PRODUCT_NOT_EXISTS, HttpStatus.NOT_FOUND,
            TechnicalMessage.FRANCHISE_ALREADY_EXISTS, HttpStatus.CONFLICT,
            TechnicalMessage.BRANCH_ALREADY_EXISTS, HttpStatus.CONFLICT,
            TechnicalMessage.PRODUCT_ALREADY_EXISTS, HttpStatus.CONFLICT
    );

    private final BuildErrorResponse buildErrorResponse;

    public Mono<ServerResponse> applyErrorHandling(Mono<ServerResponse> mono) {
        return mono
                .onErrorResume(BusinessException.class, ex -> clientError(
                        STATUS_BY_MESSAGE.getOrDefault(ex.getTechnicalMessage(), HttpStatus.BAD_REQUEST),
                        ex.getTechnicalMessage()))
                .onErrorResume(ServerWebInputException.class, ex -> clientError(
                        HttpStatus.BAD_REQUEST, TechnicalMessage.INVALID_REQUEST))
                .onErrorResume(DuplicateKeyException.class, ex -> clientError(
                        HttpStatus.CONFLICT, TechnicalMessage.RESOURCE_ALREADY_EXISTS))
                .onErrorResume(CallNotPermittedException.class, this::serviceUnavailable)
                .onErrorResume(TimeoutException.class, this::serviceUnavailable)
                .onErrorResume(DataAccessResourceFailureException.class, this::serviceUnavailable)
                .onErrorResume(this::unexpectedError);
    }

    private Mono<ServerResponse> clientError(HttpStatus status, TechnicalMessage technicalMessage) {
        log.warn("Request rejected with status {}: {}", status.value(), technicalMessage.getMessage());
        return buildErrorResponse.buildErrorResponse(status, technicalMessage);
    }

    private Mono<ServerResponse> serviceUnavailable(Throwable error) {
        log.error("Persistence unavailable: {}", error.toString());
        return buildErrorResponse.buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, TechnicalMessage.SERVICE_UNAVAILABLE);
    }

    private Mono<ServerResponse> unexpectedError(Throwable error) {
        log.error("Unexpected error while processing the request", error);
        return buildErrorResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, TechnicalMessage.INTERNAL_ERROR);
    }
}
