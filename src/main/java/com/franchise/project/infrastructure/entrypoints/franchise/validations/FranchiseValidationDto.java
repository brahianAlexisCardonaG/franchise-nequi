package com.franchise.project.infrastructure.entrypoints.franchise.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDtoUpdateName;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FranchiseValidationDto {

    public Mono<FranchiseDto> validateFieldNotNullOrBlank(FranchiseDto dto) {
        if ( dto.getName() == null ) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }

    public Mono<FranchiseDtoUpdateName> validateFranchiseDtoNameNotNullOrBlank(FranchiseDtoUpdateName dto) {
        if ( dto.getId()==null || dto.getName() == null ) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }

}
