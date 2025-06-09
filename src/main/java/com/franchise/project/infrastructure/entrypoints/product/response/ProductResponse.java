package com.franchise.project.infrastructure.entrypoints.product.response;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private BigInteger stock;
    private Long branchId;
}