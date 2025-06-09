package com.franchise.project.infrastructure.entrypoints.product.mapper;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateStock;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toProductCreate(ProductDto productDto);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toProductUpdateStock(ProductDtoUpdateStock productDtoUpdateStock);

    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toProductUpdateName(ProductDtoUpdateName productDtoUpdateName);
}
