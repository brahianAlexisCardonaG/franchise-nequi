package com.franchise.project.application.config.common;

import com.franchise.project.domain.util.ValidationCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public ValidationCondition validationCondition() {
        return new ValidationCondition();
    }
}
