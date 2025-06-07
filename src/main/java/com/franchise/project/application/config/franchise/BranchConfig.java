package com.franchise.project.application.config.franchise;

import com.franchise.project.domain.branch.api.BranchServicePort;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.branch.usecase.BranchUseCase;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.BranchPersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BranchConfig {
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;


    @Bean
    public BranchPersistencePort branchPersistencePort() {
        return new BranchPersistenceAdapter(branchRepository, branchEntityMapper);
    }

    @Bean
    public BranchServicePort branchServicePort(BranchPersistencePort branchPersistencePort,
                                               FranchisePersistencePort franchisePersistencePort) {
        return new BranchUseCase(branchPersistencePort,franchisePersistencePort);
    }
}
