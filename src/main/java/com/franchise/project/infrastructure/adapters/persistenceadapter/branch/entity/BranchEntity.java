package com.franchise.project.infrastructure.adapters.persistenceadapter.branch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "branch")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {
    @Id
    private Long id;
    private String name;
    @Column(value = "franchise_id")
    private Long franchiseId;
}
