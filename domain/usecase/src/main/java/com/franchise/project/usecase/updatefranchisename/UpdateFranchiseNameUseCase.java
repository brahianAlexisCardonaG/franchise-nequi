package com.franchise.project.usecase.updatefranchisename;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ValidationCondition validationCondition;

    public Mono<Franchise> updateFranchiseName(Franchise franchise) {
        return franchiseRepository.findById(franchise.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(existing -> franchiseRepository.existsByName(franchise.getName())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        .then(Mono.defer(() -> franchiseRepository.updateFranchise(
                                existing.toBuilder().name(franchise.getName()).build()))));
    }
}
