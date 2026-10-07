package com.franchise.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OpenApiDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void apiDocsDescribeEveryEndpoint() {
        webTestClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.paths['/api/v1/franchise'].post.operationId").isEqualTo("createFranchise")
                .jsonPath("$.paths['/api/v1/franchise/name'].put.operationId").isEqualTo("updateFranchiseName")
                .jsonPath("$.paths['/api/v1/franchise/{franchiseId}/top-stock-products'].get.operationId")
                .isEqualTo("getFranchiseIdBranchesProducts")
                .jsonPath("$.paths['/api/v1/branch'].post.operationId").isEqualTo("createBranch")
                .jsonPath("$.paths['/api/v1/branch/name'].put.operationId").isEqualTo("updateBranchName")
                .jsonPath("$.paths['/api/v1/product'].post.operationId").isEqualTo("createProduct")
                .jsonPath("$.paths['/api/v1/product/{productId}'].delete.operationId").isEqualTo("deleteProductBranch")
                .jsonPath("$.paths['/api/v1/product/stock'].put.operationId").isEqualTo("updateProductStock")
                .jsonPath("$.paths['/api/v1/product/name'].put.operationId").isEqualTo("updateProductName");
    }

    @Test
    void apiDocsDescribeErrorResponses() {
        webTestClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.paths['/api/v1/franchise'].post.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/franchise/{franchiseId}/top-stock-products'].get.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/franchise/{franchiseId}/top-stock-products'].get.parameters[0].in").isEqualTo("path")
                .jsonPath("$.paths['/api/v1/branch'].post.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/product'].post.responses['400']").exists()
                .jsonPath("$.paths['/api/v1/product/{productId}'].delete.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/product/stock'].put.responses['503']").exists()
                .jsonPath("$.paths['/api/v1/product/name'].put.responses['500']").exists()
                .jsonPath("$.components.schemas.ProductDto.required").isArray();
    }

    @Test
    void swaggerUiIsServed() {
        webTestClient.get().uri("/webjars/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }
}
