package com.claimswift.payment.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Feign Configuration
 * 
 * Configuration for Feign clients used for inter-service communication
 */
@Configuration
public class FeignConfig {

    /**
     * Configure Feign logging level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Configure Feign request timeout
     */
//    @Bean
//    public Request.Options requestOptions() {
//        return new Request.Options(
//                5000, TimeUnit.MILLISECONDS,  // Connect timeout
//                10000, TimeUnit.MILLISECONDS   // Read timeout
//        );
//    }
}
