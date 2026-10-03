package com.franchise.project.infrastructure.entrypoints.franchise.validations;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDtoUpdateName;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class FranchiseValidationDto {

    public Mono<FranchiseDto> validateFieldNotNullOrBlank(FranchiseDto dto) {
        return Mono.just(dto)
                .filter(franchise -> Objects.nonNull(franchise.getName()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

    public Mono<FranchiseDtoUpdateName> validateFranchiseDtoNameNotNullOrBlank(FranchiseDtoUpdateName dto) {
        return Mono.just(dto)
                .filter(franchise -> Objects.nonNull(franchise.getId()) && Objects.nonNull(franchise.getName()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }

}
