package com.franchise.project.infrastructure.adapters.persistenceadapter.product.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Table("product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    private Long id;
    private String name;
    private BigInteger stock;
    @Column(value = "branch_id")
    private Long branchId;
}
