package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.api.CreateFranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase implements CreateFranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchisePersistencePort.existsByName(franchise.getName())
                .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .then(Mono.defer(() -> franchisePersistencePort.createFranchise(franchise)));
    }
}
