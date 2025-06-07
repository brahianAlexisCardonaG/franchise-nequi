package com.franchise.project.domain.exception;

import com.franchise.project.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class ProcessorException extends RuntimeException {
    private final TechnicalMessage technicalMessage;

    public ProcessorException(Throwable cause, TechnicalMessage message) {
        super(cause);
        technicalMessage = message;
    }

    public ProcessorException(String message,
                              TechnicalMessage technicalMessage) {

        super(message);
        this.technicalMessage = technicalMessage;
    }
}
