package com.franchise.project.api.util.validation;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
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
