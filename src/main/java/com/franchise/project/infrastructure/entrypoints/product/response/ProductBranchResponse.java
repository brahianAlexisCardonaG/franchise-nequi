package com.franchise.project.infrastructure.entrypoints.product.response;

import com.franchise.project.infrastructure.entrypoints.branch.response.BranchResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class ProductBranchResponse {
    private Long id;
    private String name;
    private BigInteger stock;
    private BranchResponse branch;
}
