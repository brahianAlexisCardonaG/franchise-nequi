package com.franchise.project.infrastructure.entrypoints.branch.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDtoUpdateName;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class BranchValidationDto {
    public Mono<BranchDto> validateFieldNotNullOrBlank(BranchDto dto) {
        return Mono.just(dto)
                .filter(branch -> Objects.nonNull(branch.getName()) && Objects.nonNull(branch.getFranchiseId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

    public Mono<BranchDtoUpdateName> validateDtoBranchNameNotNullOrBlank(BranchDtoUpdateName dto) {
        return Mono.just(dto)
                .filter(branch -> Objects.nonNull(branch.getId()) && Objects.nonNull(branch.getName()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }
}
