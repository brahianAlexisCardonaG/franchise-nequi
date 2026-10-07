package com.franchise.project.usecase.updateproductstock;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;
    private final ValidationCondition validationCondition;

    public Mono<Product> updateProductStock(Product product) {
        return validationCondition.validate(product, Product::hasNonNegativeStock, TechnicalMessage.PRODUCT_STOCK_INVALID)
                .flatMap(validProduct -> productRepository.findById(validProduct.getId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing -> productRepository.updateProduct(
                        existing.toBuilder().stock(product.getStock()).build()));
    }
}
