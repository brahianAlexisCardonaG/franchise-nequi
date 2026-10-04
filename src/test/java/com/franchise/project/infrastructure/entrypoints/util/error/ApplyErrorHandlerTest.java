package com.franchise.project.infrastructure.entrypoints.util.error;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class ApplyErrorHandlerTest {

    private static final String STACK_FRAME = "\tat ";

    private final ApplyErrorHandler applyErrorHandler = new ApplyErrorHandler(new BuildErrorResponse());

    @Test
    void businessErrorsAreLoggedAsWarningsWithoutStackTrace(CapturedOutput output) {
        StepVerifier.create(applyErrorHandler.applyErrorHandling(
                        Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(404))
                .verifyComplete();

        assertThat(output.getOut())
                .contains("WARN")
                .contains("Request rejected with status 404: The franchise does not exist")
                .doesNotContain(STACK_FRAME);
    }

    @Test
    void unavailablePersistenceIsLoggedAsErrorWithoutStackTrace(CapturedOutput output) {
        StepVerifier.create(applyErrorHandler.applyErrorHandling(Mono.<ServerResponse>error(new TimeoutException("db"))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(503))
                .verifyComplete();

        assertThat(output.getOut())
                .contains("ERROR")
                .contains("Persistence unavailable")
                .doesNotContain(STACK_FRAME);
    }

    @Test
    void unexpectedErrorsAreLoggedAsErrorsWithStackTrace(CapturedOutput output) {
        StepVerifier.create(applyErrorHandler.applyErrorHandling(
                        Mono.<ServerResponse>error(new IllegalStateException("broken invariant"))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();

        assertThat(output.getOut())
                .contains("ERROR")
                .contains("Unexpected error while processing the request")
                .contains("java.lang.IllegalStateException: broken invariant")
                .contains(STACK_FRAME);
    }
}
