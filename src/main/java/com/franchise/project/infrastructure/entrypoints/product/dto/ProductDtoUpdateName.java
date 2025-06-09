package com.franchise.project.infrastructure.entrypoints.product.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProductDtoUpdateName {
    private Long id;
    private String name;
}
