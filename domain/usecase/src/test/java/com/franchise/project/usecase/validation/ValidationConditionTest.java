package com.franchise.project.usecase.validation;

import com.franchise.project.model.enums.TechnicalMessage;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static com.franchise.project.usecase.BusinessErrors.businessError;

class ValidationConditionTest {

    private final ValidationCondition validationCondition = new ValidationCondition();

    @Test
    void validateEmitsTheValueWhenTheRuleHolds() {
        StepVerifier.create(validationCondition.validate(5, value -> value > 0, TechnicalMessage.PRODUCT_STOCK_INVALID))
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    void validateFailsWithTheGivenMessageWhenTheRuleIsBroken() {
        StepVerifier.create(validationCondition.validate(-1, value -> value > 0, TechnicalMessage.PRODUCT_STOCK_INVALID))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_STOCK_INVALID))
                .verify();
    }

    @Test
    void rejectIfExistsCompletesWhenNothingExists() {
        StepVerifier.create(validationCondition.rejectIfExists(false, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verifyComplete();
    }

    @Test
    void rejectIfExistsFailsWhenTheResourceExists() {
        StepVerifier.create(validationCondition.rejectIfExists(true, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
    }
}
