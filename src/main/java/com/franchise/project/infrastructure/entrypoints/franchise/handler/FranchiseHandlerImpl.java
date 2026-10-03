package com.franchise.project.infrastructure.entrypoints.franchise.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapper;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapperResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseBranchProductResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.FRANCHISE_ID_PATH_VARIABLE;
import static com.franchise.project.infrastructure.entrypoints.util.Constants.REQUEST_FAILED_LOG;

@Component
@RequiredArgsConstructor
@Slf4j
public class FranchiseHandlerImpl {

    private final RequestValidator requestValidator;
    private final FranchiseMapper franchiseMapper;
    private final FranchiseMapperResponse franchiseMapperResponse;
    private final FranchiseServicePort franchiseServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(FranchiseDto.class)
                .flatMap(requestValidator::validate)
                .map(franchiseMapper::toFranchise)
                .flatMap(franchiseServicePort::createFranchise)
                .map(franchiseMapperResponse::toFranchiseResponse)
                .flatMap(franchise -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiFranchiseResponse.builder()
                                .code(TechnicalMessage.FRANCHISE_CREATED.getCode())
                                .message(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                                .date(Instant.now().toString())
                                .data(franchise)
                                .build()))
                .doOnError(ex -> log.error(REQUEST_FAILED_LOG, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> getFranchiseIdBranchesProducts(ServerRequest request) {
        Mono<ServerResponse> response = Mono.fromCallable(() -> Long.parseLong(request.pathVariable(FRANCHISE_ID_PATH_VARIABLE)))
                .onErrorMap(NumberFormatException.class, ex -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS))
                .flatMap(franchiseServicePort::getFranchiseBranchProduct)
                .map(franchiseMapperResponse::toFranchiseBranchProductListResponse)
                .flatMap(franchise -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiFranchiseBranchProductResponse.builder()
                                .code(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getCode())
                                .message(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getMessage())
                                .date(Instant.now().toString())
                                .data(franchise)
                                .build()))
                .doOnError(ex -> log.error(REQUEST_FAILED_LOG, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(FranchiseDtoUpdateName.class)
                .flatMap(requestValidator::validate)
                .map(franchiseMapper::toFranchiseUpdateName)
                .flatMap(franchiseServicePort::updateName)
                .map(franchiseMapperResponse::toFranchiseResponse)
                .flatMap(franchise -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiFranchiseResponse.builder()
                                .code(TechnicalMessage.FRANCHISE_UPDATE.getCode())
                                .message(TechnicalMessage.FRANCHISE_UPDATE.getMessage())
                                .date(Instant.now().toString())
                                .data(franchise)
                                .build()))
                .doOnError(ex -> log.error(REQUEST_FAILED_LOG, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
