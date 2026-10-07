package com.franchise.project.model.branch;

import com.franchise.project.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchProduct {
    private Long id;
    private String name;
    private Product product;
}
