package com.franchise.project.infrastructure.entrypoints.product.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductValidationDto {

    public Mono<ProductDto> validateFieldNotNullOrBlank(ProductDto dto) {
        if ( dto.getName() == null || dto.getStock() == null || dto.getBranchId() == null) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }

}
