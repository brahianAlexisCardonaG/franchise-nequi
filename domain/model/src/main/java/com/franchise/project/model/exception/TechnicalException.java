package com.franchise.project.model.exception;

import com.franchise.project.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public TechnicalException(TechnicalMessage technicalMessage, Throwable cause) {
        super(technicalMessage.getMessage(), cause);
        this.technicalMessage = technicalMessage;
    }
}
