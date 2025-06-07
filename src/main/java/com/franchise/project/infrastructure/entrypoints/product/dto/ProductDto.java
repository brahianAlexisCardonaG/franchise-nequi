package com.franchise.project.infrastructure.entrypoints.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductDto {
    private String name;
    private BigInteger stock;
    private Long branchId;
}
