package com.franchise.project.application.config.franchise;

import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.franchise.usecase.FranchiseUseCase;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.FranchisePersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FranchiseConfig {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;


    @Bean
    public FranchisePersistencePort franchisePersistencePort() {
        return new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper);
    }

    @Bean
    public FranchiseServicePort franchiseServicePort(FranchisePersistencePort franchisePersistencePort) {
        return new FranchiseUseCase(franchisePersistencePort);
    }
}
