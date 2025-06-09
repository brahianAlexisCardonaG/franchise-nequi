package com.franchise.project.infrastructure.entrypoints.branch.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDtoUpdateName;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BranchValidationDto {
    public Mono<BranchDto> validateFieldNotNullOrBlank(BranchDto dto) {
        if ( dto.getName() == null || dto.getFranchiseId() == null ) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }

    public Mono<BranchDtoUpdateName> validateDtoBranchNameNotNullOrBlank(BranchDtoUpdateName dto) {
        if ( dto.getId() == null || dto.getName() == null ) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }
}
