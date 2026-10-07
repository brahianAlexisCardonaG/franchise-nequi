package com.franchise.project.api.franchise.mapper;

import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.FranchiseBranchProductList;
import com.franchise.project.api.franchise.response.FranchiseBranchProductListResponse;
import com.franchise.project.api.franchise.response.FranchiseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapperResponse {
    FranchiseResponse toFranchiseResponse(Franchise franchise);

    FranchiseBranchProductListResponse toFranchiseBranchProductListResponse(
            FranchiseBranchProductList franchiseBranchProductList);
}
