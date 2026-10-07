package com.franchise.project.usecase.gettopstockproducts;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchProduct;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.franchise.FranchiseBranchProductList;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.function.BinaryOperator;

@RequiredArgsConstructor
public class GetTopStockProductsUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<FranchiseBranchProductList> getTopStockProducts(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise -> branchRepository.findBranchesByFranchiseId(franchise.getId())
                        .flatMap(this::findLargestStockProduct)
                        .collectList()
                        .map(branches -> FranchiseBranchProductList.builder()
                                .id(franchise.getId())
                                .name(franchise.getName())
                                .branches(branches)
                                .build()));
    }

    private Mono<BranchProduct> findLargestStockProduct(Branch branch) {
        return productRepository.findProductByBranchId(branch.getId())
                .reduce(BinaryOperator.maxBy(Comparator.comparing(Product::getStock)))
                .map(product -> BranchProduct.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .product(product)
                        .build())
                .defaultIfEmpty(BranchProduct.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .build());
    }
}
