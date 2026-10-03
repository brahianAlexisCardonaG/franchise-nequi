package com.franchise.project.domain.franchise.api;

import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import reactor.core.publisher.Mono;

public interface GetTopStockProductsServicePort {
    Mono<FranchiseBranchProductList> getTopStockProducts(Long franchiseId);
}
