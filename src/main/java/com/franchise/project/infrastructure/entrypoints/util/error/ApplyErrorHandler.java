package com.franchise.project.infrastructure.entrypoints.util.error;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeoutException;

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
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse.buildErrorResponse(
                        STATUS_BY_MESSAGE.getOrDefault(ex.getTechnicalMessage(), HttpStatus.BAD_REQUEST),
                        ex.getTechnicalMessage()))
                .onErrorResume(ServerWebInputException.class, ex -> buildErrorResponse.buildErrorResponse(
                        HttpStatus.BAD_REQUEST, TechnicalMessage.INVALID_REQUEST))
                .onErrorResume(DuplicateKeyException.class, ex -> buildErrorResponse.buildErrorResponse(
                        HttpStatus.CONFLICT, TechnicalMessage.RESOURCE_ALREADY_EXISTS))
                .onErrorResume(CallNotPermittedException.class, ex -> buildErrorResponse.buildErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE, TechnicalMessage.SERVICE_UNAVAILABLE))
                .onErrorResume(TimeoutException.class, ex -> buildErrorResponse.buildErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE, TechnicalMessage.SERVICE_UNAVAILABLE))
                .onErrorResume(ex -> buildErrorResponse.buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, TechnicalMessage.INTERNAL_ERROR));
    }
}
