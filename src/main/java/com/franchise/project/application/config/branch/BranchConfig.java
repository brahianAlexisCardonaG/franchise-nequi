package com.franchise.project.application.config.branch;

import com.franchise.project.domain.branch.api.CreateBranchServicePort;
import com.franchise.project.domain.branch.api.UpdateBranchNameServicePort;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.branch.usecase.CreateBranchUseCase;
import com.franchise.project.domain.branch.usecase.UpdateBranchNameUseCase;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.BranchPersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BranchConfig {

    @Bean
    public BranchPersistencePort branchPersistencePort(BranchRepository branchRepository,
                                                       BranchEntityMapper branchEntityMapper,
                                                       PersistenceResilience persistenceResilience) {
        return new BranchPersistenceAdapter(branchRepository, branchEntityMapper, persistenceResilience);
    }

    @Bean
    public CreateBranchServicePort createBranchServicePort(BranchPersistencePort branchPersistencePort,
                                                           FranchisePersistencePort franchisePersistencePort,
                                                           ValidationCondition validationCondition) {
        return new CreateBranchUseCase(branchPersistencePort, franchisePersistencePort, validationCondition);
    }

    @Bean
    public UpdateBranchNameServicePort updateBranchNameServicePort(BranchPersistencePort branchPersistencePort,
                                                                   ValidationCondition validationCondition) {
        return new UpdateBranchNameUseCase(branchPersistencePort, validationCondition);
    }
}
