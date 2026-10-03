package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository.FranchiseRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchisePersistencePort {
    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return persistenceResilience.write(franchiseRepository.save(franchiseEntityMapper.toEntity(franchise)))
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> findByName(String name) {
        return persistenceResilience.read(franchiseRepository.existsByName(name));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return persistenceResilience.read(franchiseRepository.findById(id))
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> updateFranchise(Franchise franchise) {
        return persistenceResilience.write(franchiseRepository.save(franchiseEntityMapper.toEntity(franchise)))
                .map(franchiseEntityMapper::toModel);
    }
}
