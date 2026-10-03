package com.franchise.project.domain.exception;

import com.franchise.project.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage());
        this.technicalMessage = technicalMessage;
    }
}
