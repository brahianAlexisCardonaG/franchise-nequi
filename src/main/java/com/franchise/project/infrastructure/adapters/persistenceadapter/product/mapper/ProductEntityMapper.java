package com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    Product toModel(ProductEntity productEntity);
    ProductEntity toEntity(Product product);
}
