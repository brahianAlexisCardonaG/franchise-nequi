package com.franchise.project.application.config.franchise;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.franchise.api.CreateFranchiseServicePort;
import com.franchise.project.domain.franchise.api.GetTopStockProductsServicePort;
import com.franchise.project.domain.franchise.api.UpdateFranchiseNameServicePort;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.franchise.usecase.CreateFranchiseUseCase;
import com.franchise.project.domain.franchise.usecase.GetTopStockProductsUseCase;
import com.franchise.project.domain.franchise.usecase.UpdateFranchiseNameUseCase;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.FranchisePersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository.FranchiseRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FranchiseConfig {

    @Bean
    public FranchisePersistencePort franchisePersistencePort(FranchiseRepository franchiseRepository,
                                                             FranchiseEntityMapper franchiseEntityMapper,
                                                             PersistenceResilience persistenceResilience) {
        return new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper, persistenceResilience);
    }

    @Bean
    public CreateFranchiseServicePort createFranchiseServicePort(FranchisePersistencePort franchisePersistencePort,
                                                                 ValidationCondition validationCondition) {
        return new CreateFranchiseUseCase(franchisePersistencePort, validationCondition);
    }

    @Bean
    public GetTopStockProductsServicePort getTopStockProductsServicePort(FranchisePersistencePort franchisePersistencePort,
                                                                         BranchPersistencePort branchPersistencePort,
                                                                         ProductPersistencePort productPersistencePort) {
        return new GetTopStockProductsUseCase(franchisePersistencePort, branchPersistencePort, productPersistencePort);
    }

    @Bean
    public UpdateFranchiseNameServicePort updateFranchiseNameServicePort(FranchisePersistencePort franchisePersistencePort,
                                                                         ValidationCondition validationCondition) {
        return new UpdateFranchiseNameUseCase(franchisePersistencePort, validationCondition);
    }
}
