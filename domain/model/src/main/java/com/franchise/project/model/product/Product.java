package com.franchise.project.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private Long branchId;

    public boolean hasNonNegativeStock() {
        return stock >= 0;
    }
}
