package com.franchise.project.infrastructure.entrypoints.product.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateStock;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class ProductValidationDto {

    public Mono<ProductDto> validateDtoCreateNotNullOrBlank(ProductDto dto) {
        return Mono.just(dto)
                .filter(product -> Objects.nonNull(product.getName())
                        && Objects.nonNull(product.getStock())
                        && Objects.nonNull(product.getBranchId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

    public Mono<ProductDtoUpdateStock> validateDtoUpdateStockNotNullOrBlank(ProductDtoUpdateStock dto){
        return Mono.just(dto)
                .filter(product -> Objects.nonNull(product.getId()) && Objects.nonNull(product.getStock()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

    public Mono<ProductDtoUpdateName> validateDtoUpdateNameNotNullOrBlank(ProductDtoUpdateName dto){
        return Mono.just(dto)
                .filter(product -> Objects.nonNull(product.getId()) && Objects.nonNull(product.getName()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

}
