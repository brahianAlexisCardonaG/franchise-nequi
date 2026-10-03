package com.franchise.project.infrastructure.entrypoints.util.error;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

    private final BuildErrorResponse buildErrorRes;

    public Mono<ServerResponse> applyErrorHandling(Mono<ServerResponse> mono) {
        return mono
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        STATUS_BY_MESSAGE.getOrDefault(ex.getTechnicalMessage(), HttpStatus.BAD_REQUEST),
                        ex.getTechnicalMessage()))
                .onErrorResume(ServerWebInputException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST, TechnicalMessage.INVALID_REQUEST))
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, TechnicalMessage.INTERNAL_ERROR));
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, TechnicalMessage technicalMessage) {
        return buildErrorRes.buildErrorResponse(status, technicalMessage, List.of(ErrorDto.builder()
                .code(technicalMessage.getCode())
                .message(technicalMessage.getMessage())
                .param(technicalMessage.getParam())
                .build()));
    }
}
