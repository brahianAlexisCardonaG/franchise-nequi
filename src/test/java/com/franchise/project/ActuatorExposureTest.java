package com.franchise.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ActuatorExposureTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void healthEndpointIsExposedWithoutDetails() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectBody()
                .jsonPath("$.status").exists()
                .jsonPath("$.components").doesNotExist();
    }

    @Test
    void sensitiveEndpointsAreNotExposed() {
        webTestClient.get().uri("/actuator/env")
                .exchange()
                .expectStatus().isNotFound();
    }
}
