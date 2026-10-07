package com.franchise.project.api.product.mapper;

import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.ProductBranch;
import com.franchise.project.api.product.response.ProductBranchResponse;
import com.franchise.project.api.product.response.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapperResponse {
    ProductBranchResponse toProductBranchResponse(ProductBranch productBranch);
    ProductResponse toProductResponse(Product product);
}
