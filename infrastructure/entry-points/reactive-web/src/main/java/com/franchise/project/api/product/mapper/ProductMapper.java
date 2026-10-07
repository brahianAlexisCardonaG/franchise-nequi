package com.franchise.project.api.product.mapper;

import com.franchise.project.model.product.Product;
import com.franchise.project.api.product.dto.ProductDto;
import com.franchise.project.api.product.dto.ProductDtoUpdateName;
import com.franchise.project.api.product.dto.ProductDtoUpdateStock;
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
