package com.franchise.project.usecase.validation;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public class ValidationCondition {

    public <T> Mono<T> validate(T value, Predicate<T> rule, TechnicalMessage technicalMessage) {
        return Mono.just(value)
                .filter(rule)
                .switchIfEmpty(Mono.error(() -> new BusinessException(technicalMessage)));
    }

    public Mono<Void> rejectIfExists(Boolean exists, TechnicalMessage technicalMessage) {
        return validate(exists, Boolean.FALSE::equals, technicalMessage).then();
    }
}
