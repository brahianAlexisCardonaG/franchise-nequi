package com.franchise.project.infrastructure.entrypoints.product.handler;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDtoUpdateStock;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapperResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductBranchResponse;
import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import com.franchise.project.infrastructure.entrypoints.product.validations.ProductValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductHandlerImplTest {
    @Mock
    private ProductValidationDto productValidationDto;
    @Mock private ProductMapper productMapper;
    @Mock private ProductMapperResponse productMapperResponse;
    @Mock private ProductServicePort productServicePort;
    @Mock private ApplyErrorHandler applyErrorHandler;

    private ProductHandlerImpl handler;
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        handler = new ProductHandlerImpl(
                productValidationDto,
                productMapper,
                productMapperResponse,
                productServicePort,
                applyErrorHandler
        );

        var router = RouterFunctions
                .route(RequestPredicates.POST("/api/v1/product"), handler::createProduct)
                .andRoute(RequestPredicates.DELETE("/api/v1/product"), handler::deleteProductBranch)
                .andRoute(RequestPredicates.PUT("/api/v1/product/stock"), handler::updateProductStock)
                .andRoute(RequestPredicates.PUT("/api/v1/product/name"), handler::updateProductName);

        webClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void createProduct_shouldReturnCreated() {
        ProductDto dto = new ProductDto("Producto X", BigInteger.TEN, 5L);
        Product domain = new Product(null, "Producto X", BigInteger.TEN, 5L);
        ProductBranch productBranch = new ProductBranch(1L, "Producto X", BigInteger.TEN,
                new Branch(5L, "Sucursal A", 1L));
        ProductBranchResponse response = ProductBranchResponse.builder()
                .id(1L).name("Producto X").stock(BigInteger.TEN)
                .branch(new BranchResponse(5L, "Sucursal A")).build();

        when(productValidationDto.validateDtoCreateNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(productMapper.toProductCreate(dto)).thenReturn(domain);
        when(productServicePort.createProduct(any(Product.class))).thenReturn(Mono.just(productBranch));
        when(productMapperResponse.toProductBranchResponse(productBranch)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.post()
                .uri("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_CREATED.getCode())
                .jsonPath("$.data.name").isEqualTo("Producto X")
                .jsonPath("$.data.branch.id").isEqualTo(5);
    }

    @Test
    void deleteProductBranch_shouldReturnCreatedMessage() {
        Long productId = 99L;

        when(productServicePort.deleteProductBranch(productId)).thenReturn(Mono.empty());
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/product")
                        .queryParam("productId", productId.toString())
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_BRANCH_DELETE.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_BRANCH_DELETE.getMessage());
    }

    @Test
    void updateProductStock_shouldReturnCreatedWithProductData() {
        ProductDtoUpdateStock dto = new ProductDtoUpdateStock();
        dto.setId(1L);
        dto.setStock(BigInteger.valueOf(50));

        Product product = new Product(1L, "Producto X", BigInteger.valueOf(50), 2L);
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Producto X");
        response.setStock(BigInteger.valueOf(50));

        when(productValidationDto.validateDtoUpdateStockNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(productMapper.toProductUpdateStock(dto)).thenReturn(product);
        when(productServicePort.updateStock(any(Product.class))).thenReturn(Mono.just(product));
        when(productMapperResponse.toProductResponse(product)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.put()
                .uri("/api/v1/product/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_UPDATE.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.stock").isEqualTo(50);
    }

    @Test
    void updateProductName_shouldReturnOkWithUpdatedProduct() {
        ProductDtoUpdateName dto = new ProductDtoUpdateName();
        dto.setId(1L);
        dto.setName("Nuevo Nombre");

        Product updated = new Product(1L, "Nuevo Nombre", BigInteger.valueOf(20), 3L);
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Nuevo Nombre");
        response.setStock(BigInteger.valueOf(20));

        when(productValidationDto.validateDtoUpdateNameNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(productMapper.toProductUpdateName(dto)).thenReturn(updated);
        when(productServicePort.updateName(any(Product.class))).thenReturn(Mono.just(updated));
        when(productMapperResponse.toProductResponse(updated)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.put()
                .uri("/api/v1/product/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_UPDATE.getCode())
                .jsonPath("$.data.name").isEqualTo("Nuevo Nombre");
    }

}