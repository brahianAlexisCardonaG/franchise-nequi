package com.franchise.project.infrastructure.entrypoints.product.mapper;

import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductBranchResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductBranchMapperResponse {
    ProductBranchResponse toProductBranchResponse(ProductBranch productBranch);
}
