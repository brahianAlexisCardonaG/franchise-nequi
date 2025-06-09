package com.franchise.project.infrastructure.entrypoints.franchise.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapper;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapperResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseBranchProductResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.validations.FranchiseValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.FRANCHISE_ERROR;
import static com.franchise.project.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Log4j2
public class FranchiseHandlerImpl {

    private final FranchiseValidationDto franchiseValidationDto;
    private final FranchiseMapper franchiseMapper;
    private final FranchiseMapperResponse franchiseMapperResponse;
    private final FranchiseServicePort franchiseServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(FranchiseDto.class)
                .flatMap(franchiseValidationDto::validateFieldNotNullOrBlank)
                .map(franchiseMapper::toFranchise)
                .flatMap(franchise -> franchiseServicePort.createFranchise(Mono.just(franchise)))
                .map(franchiseMapperResponse::toFranchiseResponse)
                .flatMap( franchise ->
                            ServerResponse.status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(ApiFranchiseResponse.builder()
                                            .code(TechnicalMessage.FRANCHISE_CREATED.getCode())
                                            .message(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                                            .date(Instant.now().toString())
                                            .data(franchise)
                                            .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> getFranchiseIdBranchesProducts(ServerRequest request) {

        Long franchiseId = request
                .queryParam("franchiseId")
                .map(Long::parseLong)
                .orElseThrow(() -> new BusinessException(TechnicalMessage.FRANCHISE_ID_REQUIRED));

        Mono<ServerResponse> response = franchiseServicePort.getFranchiseBranchProduct(franchiseId)
                .map(franchiseMapperResponse::toFranchiseBranchProductListResponse)
                .flatMap(franchise ->
                ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiFranchiseBranchProductResponse.builder()
                                .code(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getCode())
                                .message(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getMessage())
                                .date(Instant.now().toString())
                                .data(franchise)
                                .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);

    }
}
