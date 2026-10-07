package com.franchise.project.usecase.createfranchise;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ValidationCondition validationCondition;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.existsByName(franchise.getName())
                .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .then(Mono.defer(() -> franchiseRepository.createFranchise(franchise)));
    }
}
