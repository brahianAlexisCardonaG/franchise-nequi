package com.franchise.project.model.product;

import com.franchise.project.model.branch.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBranch {
    private Long id;
    private String name;
    private Integer stock;
    private Branch branch;
}
