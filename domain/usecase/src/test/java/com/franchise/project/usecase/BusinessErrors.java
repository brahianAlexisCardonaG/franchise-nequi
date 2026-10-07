package com.franchise.project.usecase;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;

import java.util.function.Predicate;

public final class BusinessErrors {

    private BusinessErrors() {
    }

    public static Predicate<Throwable> businessError(TechnicalMessage expected) {
        return error -> error instanceof BusinessException businessException
                && businessException.getTechnicalMessage() == expected;
    }
}
