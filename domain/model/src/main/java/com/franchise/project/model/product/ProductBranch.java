package com.franchise.project.model.product;

import com.franchise.project.model.branch.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class ProductBranch {
    private Long id;
    private String name;
    private Integer stock;
    private Branch branch;
}
