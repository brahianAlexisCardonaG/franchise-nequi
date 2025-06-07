package com.franchise.project.infrastructure.entrypoints.product.mapper;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductDto productDto);
}
