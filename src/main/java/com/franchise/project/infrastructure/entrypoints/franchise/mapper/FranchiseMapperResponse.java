package com.franchise.project.infrastructure.entrypoints.franchise.mapper;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseBranchProductListResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapperResponse {
    FranchiseResponse toFranchiseResponse(Franchise franchise);

    FranchiseBranchProductListResponse toFranchiseBranchProductListResponse(
            FranchiseBranchProductList franchiseBranchProductList);
}
