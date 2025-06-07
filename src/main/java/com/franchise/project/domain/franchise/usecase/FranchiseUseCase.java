package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;

    @Override
    public Mono<Franchise> createFranchise(Mono<Franchise> franchise) {
        return franchise
                .flatMap(fran->
                        franchisePersistencePort.findByName(fran.getName())
                                .filter(exist -> !exist)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                                .flatMap(ignore ->
                                        franchisePersistencePort.createFranchise(Mono.just(fran)))
                );
    }
}
