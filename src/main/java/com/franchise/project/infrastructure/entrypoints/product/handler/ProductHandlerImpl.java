package com.franchise.project.infrastructure.entrypoints.product.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductBranchMapperResponse;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductBranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.validations.ProductValidationDto;
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
public class ProductHandlerImpl {
    private final ProductValidationDto productValidationDto;
    private final ProductMapper productMapper;
    private final ProductBranchMapperResponse productBranchMapperResponse;
    private final ProductServicePort productServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDto.class)
                .flatMap(productValidationDto::validateFieldNotNullOrBlank)
                .map(productMapper::toProduct)
                .flatMap(prod -> productServicePort.createProduct(Mono.just(prod)))
                .map(productBranchMapperResponse::toProductBranchResponse)
                .flatMap( productResp ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiProductBranchResponse.builder()
                                        .code(TechnicalMessage.PRODUCT_CREATED.getCode())
                                        .message(TechnicalMessage.PRODUCT_CREATED.getMessage())
                                        .date(Instant.now().toString())
                                        .data(productResp)
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
