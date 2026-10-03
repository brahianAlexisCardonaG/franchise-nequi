package com.franchise.project.application.config.product;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.product.api.CreateProductServicePort;
import com.franchise.project.domain.product.api.DeleteProductServicePort;
import com.franchise.project.domain.product.api.UpdateProductNameServicePort;
import com.franchise.project.domain.product.api.UpdateProductStockServicePort;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.product.usecase.CreateProductUseCase;
import com.franchise.project.domain.product.usecase.DeleteProductUseCase;
import com.franchise.project.domain.product.usecase.UpdateProductNameUseCase;
import com.franchise.project.domain.product.usecase.UpdateProductStockUseCase;
import com.franchise.project.domain.util.ValidationCondition;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.ProductPersistenceAdapter;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public ProductPersistencePort productPersistencePort(ProductRepository productRepository,
                                                         ProductEntityMapper productEntityMapper,
                                                         PersistenceResilience persistenceResilience) {
        return new ProductPersistenceAdapter(productRepository, productEntityMapper, persistenceResilience);
    }

    @Bean
    public CreateProductServicePort createProductServicePort(BranchPersistencePort branchPersistencePort,
                                                             ProductPersistencePort productPersistencePort,
                                                             ValidationCondition validationCondition) {
        return new CreateProductUseCase(branchPersistencePort, productPersistencePort, validationCondition);
    }

    @Bean
    public DeleteProductServicePort deleteProductServicePort(ProductPersistencePort productPersistencePort) {
        return new DeleteProductUseCase(productPersistencePort);
    }

    @Bean
    public UpdateProductStockServicePort updateProductStockServicePort(ProductPersistencePort productPersistencePort,
                                                                       ValidationCondition validationCondition) {
        return new UpdateProductStockUseCase(productPersistencePort, validationCondition);
    }

    @Bean
    public UpdateProductNameServicePort updateProductNameServicePort(ProductPersistencePort productPersistencePort,
                                                                     ValidationCondition validationCondition) {
        return new UpdateProductNameUseCase(productPersistencePort, validationCondition);
    }
}
