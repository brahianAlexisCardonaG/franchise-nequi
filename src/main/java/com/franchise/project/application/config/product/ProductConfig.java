package com.franchise.project.application.config.product;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.product.usecase.ProductUseCase;
import com.franchise.project.domain.util.ValidationCondition;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.ProductPersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProductConfig {
    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;


    @Bean
    public ProductPersistencePort productPersistencePort(PersistenceResilience persistenceResilience) {
        return new ProductPersistenceAdapter(productRepository, productEntityMapper, persistenceResilience);
    }

    @Bean
    public ProductServicePort productServicePort(ProductPersistencePort productPersistencePort,
                                                 BranchPersistencePort branchPersistencePort,
                                                 ValidationCondition validationCondition
    ) {
        return new ProductUseCase(branchPersistencePort, productPersistencePort, validationCondition);
    }
}
