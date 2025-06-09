package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchisePersistencePort {
    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;

    @Override
    public Mono<Franchise> createFranchise(Mono<Franchise> franchise) {
        return franchise
                .map(franchiseEntityMapper::toEntity)
                .flatMap(franchiseRepository::save)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> findByName(String name) {
        return franchiseRepository.findByName(name)
                .map(franchiseEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return franchiseRepository.findById(id)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> updateFranchise(Mono<Franchise> franchiseMono) {
        return franchiseMono
                .map(franchiseEntityMapper::toEntity)
                .flatMap(franchiseRepository::save)
                .map(franchiseEntityMapper::toModel);
    }
}
