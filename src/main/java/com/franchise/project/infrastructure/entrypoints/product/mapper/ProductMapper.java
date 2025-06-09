package com.franchise.project.infrastructure.entrypoints.product.mapper;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdate;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toProductCreate(ProductDto productDto);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toProductUpdate(ProductDtoUpdate productDtoUpdate);

    ProductResponse toProductResponse(Product product);
}
