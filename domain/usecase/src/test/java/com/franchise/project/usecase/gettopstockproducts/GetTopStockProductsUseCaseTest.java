package com.franchise.project.usecase.gettopstockproducts;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchProduct;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.FranchiseBranchProductList;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.franchise.project.usecase.BusinessErrors.businessError;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopStockProductsUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ProductRepository productRepository;

    private GetTopStockProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTopStockProductsUseCase(franchiseRepository, branchRepository, productRepository);
    }

    @Test
    void returnsTheLargestStockProductOfEachBranch() {
        Branch downtown = new Branch(10L, "Downtown", 1L);
        Branch airport = new Branch(11L, "Airport", 1L);
        Product water = new Product(100L, "Water", 10, 10L);
        Product coffee = new Product(101L, "Coffee", 20, 10L);
        Product tea = new Product(102L, "Tea", 5, 10L);
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(branchRepository.findBranchesByFranchiseId(1L)).thenReturn(Flux.just(downtown, airport));
        when(productRepository.findProductByBranchId(10L)).thenReturn(Flux.just(water, coffee, tea));
        when(productRepository.findProductByBranchId(11L)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getTopStockProducts(1L))
                .expectNext(new FranchiseBranchProductList(1L, "Coffee House", List.of(
                        new BranchProduct(10L, "Downtown", coffee),
                        new BranchProduct(11L, "Airport", null))))
                .verifyComplete();
    }

    @Test
    void failsWhenTheFranchiseDoesNotExist() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTopStockProducts(99L))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(branchRepository, never()).findBranchesByFranchiseId(anyLong());
    }
}
