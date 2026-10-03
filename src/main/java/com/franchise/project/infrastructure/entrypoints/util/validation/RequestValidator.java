package com.franchise.project.infrastructure.entrypoints.util.validation;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T request) {
        return Mono.just(request)
                .filter(candidate -> validator.validate(candidate).isEmpty())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.INVALID_PARAMETERS)));
    }
}
