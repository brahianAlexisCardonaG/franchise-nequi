package com.franchise.project.domain.util;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ValidationCondition {
    public Mono<Void> validationExist(Boolean condition, TechnicalMessage technicalMessage) {
        if (condition) {
            return Mono.error(new BusinessException(technicalMessage));
        }
        return Mono.empty();
    }
}
