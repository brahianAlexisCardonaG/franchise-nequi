package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "franchise")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseEntity {
    @Id
    private Long id;
    private String name;
}
