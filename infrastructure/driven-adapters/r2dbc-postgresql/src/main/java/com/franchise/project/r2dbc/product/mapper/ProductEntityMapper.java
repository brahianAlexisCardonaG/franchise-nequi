package com.franchise.project.r2dbc.product.mapper;

import com.franchise.project.model.product.Product;
import com.franchise.project.r2dbc.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    Product toModel(ProductEntity productEntity);
    ProductEntity toEntity(Product product);
}
