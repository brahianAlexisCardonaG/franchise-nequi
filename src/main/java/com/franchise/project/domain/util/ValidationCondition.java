package com.franchise.project.domain.util;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import reactor.core.publisher.Mono;

public class ValidationCondition {
    public Mono<Void> validationExist(Boolean condition, TechnicalMessage technicalMessage) {
        return Mono.just(condition)
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.error(() -> new BusinessException(technicalMessage)))
                .then();
    }
}
