package com.franchise.project.infrastructure.entrypoints.product.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProductDtoUpdate {
    private Long Id;
    private BigInteger stock;
}
