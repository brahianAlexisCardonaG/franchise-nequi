package com.franchise.project.domain.branch.model;

import com.franchise.project.domain.product.model.Product;
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
