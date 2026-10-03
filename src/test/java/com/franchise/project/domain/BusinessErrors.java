package com.franchise.project.domain;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;

import java.util.function.Predicate;

public final class BusinessErrors {

    private BusinessErrors() {
    }

    public static Predicate<Throwable> businessError(TechnicalMessage expected) {
        return error -> error instanceof BusinessException businessException
                && businessException.getTechnicalMessage() == expected;
    }
}
