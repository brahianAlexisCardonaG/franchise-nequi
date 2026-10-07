package com.franchise.project.r2dbc.franchise;

import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.r2dbc.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.r2dbc.franchise.repository.FranchiseReactiveRepository;
import com.franchise.project.r2dbc.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepository {
    private final FranchiseReactiveRepository franchiseReactiveRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return persistenceResilience.write(franchiseReactiveRepository.save(franchiseEntityMapper.toEntity(franchise)))
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return persistenceResilience.read(franchiseReactiveRepository.existsByName(name));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return persistenceResilience.read(franchiseReactiveRepository.findById(id))
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> updateFranchise(Franchise franchise) {
        return persistenceResilience.write(franchiseReactiveRepository.save(franchiseEntityMapper.toEntity(franchise)))
                .map(franchiseEntityMapper::toModel);
    }
}
