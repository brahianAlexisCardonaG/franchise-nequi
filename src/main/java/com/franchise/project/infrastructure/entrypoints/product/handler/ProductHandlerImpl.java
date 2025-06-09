package com.franchise.project.infrastructure.entrypoints.product.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdate;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductBranchMapperResponse;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductBranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductResponse;
import com.franchise.project.infrastructure.entrypoints.product.validations.ProductValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.response.ApiResponseMessage;
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
import java.util.Arrays;
import java.util.List;

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
                .flatMap(productValidationDto::validateDtoCreateNotNullOrBlank)
                .map(productMapper::toProductCreate)
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

    public Mono<ServerResponse> deleteProductBranch(ServerRequest request) {
        Long productId = request
                .queryParam("productId")
                .map(Long::parseLong)
                .orElseThrow(() -> new BusinessException(TechnicalMessage.PRODUCT_ID_REQUIRED));

        Mono<ServerResponse> response = productServicePort.deleteProductBranch(productId)
                .then(ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponseMessage.builder()
                                        .code(TechnicalMessage.PRODUCT_BRANCH_DELETE.getCode())
                                        .message(TechnicalMessage.PRODUCT_BRANCH_DELETE.getMessage())
                                        .date(Instant.now().toString())
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDtoUpdate.class)
                .flatMap(productValidationDto::validateDtoUpdateNotNullOrBlank)
                .map(productMapper::toProductUpdate)
                .flatMap(prod -> productServicePort.updateStock(Mono.just(prod)))
                .map(productMapper::toProductResponse)
                .flatMap( productResp ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiProductResponse.builder()
                                        .code(TechnicalMessage.PRODUCT_UPDATE.getCode())
                                        .message(TechnicalMessage.PRODUCT_UPDATE.getMessage())
                                        .date(Instant.now().toString())
                                        .data(productResp)
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
