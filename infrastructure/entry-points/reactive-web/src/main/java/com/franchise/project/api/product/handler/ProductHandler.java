package com.franchise.project.api.product.handler;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.usecase.createproduct.CreateProductUseCase;
import com.franchise.project.usecase.deleteproduct.DeleteProductUseCase;
import com.franchise.project.usecase.updateproductname.UpdateProductNameUseCase;
import com.franchise.project.usecase.updateproductstock.UpdateProductStockUseCase;
import com.franchise.project.api.product.dto.ProductDto;
import com.franchise.project.api.product.dto.ProductDtoUpdateName;
import com.franchise.project.api.product.dto.ProductDtoUpdateStock;
import com.franchise.project.api.product.mapper.ProductMapper;
import com.franchise.project.api.product.mapper.ProductMapperResponse;
import com.franchise.project.api.product.response.ApiProductBranchResponse;
import com.franchise.project.api.product.response.ApiProductResponse;
import com.franchise.project.api.util.error.ApplyErrorHandler;
import com.franchise.project.api.util.response.ApiResponseMessage;
import com.franchise.project.api.util.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.franchise.project.api.util.Constants.PRODUCT_ID_PATH_VARIABLE;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final RequestValidator requestValidator;
    private final ProductMapper productMapper;
    private final ProductMapperResponse productMapperResponse;
    private final CreateProductUseCase createProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(ProductDto.class)
                .flatMap(requestValidator::validate)
                .map(productMapper::toProductCreate)
                .flatMap(createProductUseCase::createProduct)
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
                .flatMap(deleteProductUseCase::deleteProduct)
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
                .flatMap(updateProductStockUseCase::updateProductStock)
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
                .flatMap(updateProductNameUseCase::updateProductName)
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
