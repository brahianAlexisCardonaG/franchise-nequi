package com.franchise.project.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
