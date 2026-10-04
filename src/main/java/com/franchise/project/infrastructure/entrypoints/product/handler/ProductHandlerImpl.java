package com.franchise.project.infrastructure.entrypoints.product.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.CreateProductServicePort;
import com.franchise.project.domain.product.api.DeleteProductServicePort;
import com.franchise.project.domain.product.api.UpdateProductNameServicePort;
import com.franchise.project.domain.product.api.UpdateProductStockServicePort;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateStock;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapperResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductBranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductResponse;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.response.ApiResponseMessage;
import com.franchise.project.infrastructure.entrypoints.util.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.PRODUCT_ID_PATH_VARIABLE;

@Component
@RequiredArgsConstructor
public class ProductHandlerImpl {
    private final RequestValidator requestValidator;
    private final ProductMapper productMapper;
    private final ProductMapperResponse productMapperResponse;
    private final CreateProductServicePort createProductServicePort;
    private final DeleteProductServicePort deleteProductServicePort;
    private final UpdateProductStockServicePort updateProductStockServicePort;
    private final UpdateProductNameServicePort updateProductNameServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDto.class)
                .flatMap(requestValidator::validate)
                .map(productMapper::toProductCreate)
                .flatMap(createProductServicePort::createProduct)
                .map(productMapperResponse::toProductBranchResponse)
                .flatMap(product -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiProductBranchResponse.builder()
                                .code(TechnicalMessage.PRODUCT_CREATED.getCode())
                                .message(TechnicalMessage.PRODUCT_CREATED.getMessage())
                                .date(Instant.now().toString())
                                .data(product)
                                .build()));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> deleteProductBranch(ServerRequest request) {
        Mono<ServerResponse> response = Mono.fromCallable(() -> Long.parseLong(request.pathVariable(PRODUCT_ID_PATH_VARIABLE)))
                .onErrorMap(NumberFormatException.class, ex -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS))
                .flatMap(deleteProductServicePort::deleteProduct)
                .then(Mono.defer(() -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponseMessage.builder()
                                .code(TechnicalMessage.PRODUCT_DELETED.getCode())
                                .message(TechnicalMessage.PRODUCT_DELETED.getMessage())
                                .date(Instant.now().toString())
                                .build())));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDtoUpdateStock.class)
                .flatMap(requestValidator::validate)
                .map(productMapper::toProductUpdateStock)
                .flatMap(updateProductStockServicePort::updateProductStock)
                .map(productMapperResponse::toProductResponse)
                .flatMap(product -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiProductResponse.builder()
                                .code(TechnicalMessage.PRODUCT_UPDATE.getCode())
                                .message(TechnicalMessage.PRODUCT_UPDATE.getMessage())
                                .date(Instant.now().toString())
                                .data(product)
                                .build()));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDtoUpdateName.class)
                .flatMap(requestValidator::validate)
                .map(productMapper::toProductUpdateName)
                .flatMap(updateProductNameServicePort::updateProductName)
                .map(productMapperResponse::toProductResponse)
                .flatMap(product -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiProductResponse.builder()
                                .code(TechnicalMessage.PRODUCT_UPDATE.getCode())
                                .message(TechnicalMessage.PRODUCT_UPDATE.getMessage())
                                .date(Instant.now().toString())
                                .data(product)
                                .build()));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
