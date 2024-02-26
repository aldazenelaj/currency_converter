package com.currency.converter.app.exception;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class CircuitBreakerConfigurations {
    private static Logger log = LoggerFactory.getLogger(CircuitBreakerConfigurations.class.getName());

    @Bean
    public CircuitBreakerConfig externalConverterApiCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofSeconds(50))
                .failureRateThreshold(50)
                .recordExceptions(RuntimeException.class)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
    }

}
