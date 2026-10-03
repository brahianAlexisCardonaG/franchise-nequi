package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.UpdateFranchiseNameServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase implements UpdateFranchiseNameServicePort {

    private final FranchisePersistencePort franchisePersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Franchise> updateFranchiseName(Franchise franchise) {
        return franchisePersistencePort.findById(franchise.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(existing -> franchisePersistencePort.existsByName(franchise.getName())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        .then(Mono.defer(() -> franchisePersistencePort.updateFranchise(
                                existing.toBuilder().name(franchise.getName()).build()))));
    }
}
