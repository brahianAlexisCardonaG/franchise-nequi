package com.franchise.project.infrastructure.entrypoints.product.mapper;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductBranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapperResponse {
    ProductBranchResponse toProductBranchResponse(ProductBranch productBranch);
    ProductResponse toProductResponse(Product product);
}
