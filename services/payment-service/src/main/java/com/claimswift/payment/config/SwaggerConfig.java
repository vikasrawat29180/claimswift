package com.claimswift.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * 
 * Provides API documentation for the Payment Service
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("API documentation for ClaimSwift Payment Service. " +
                                "This service handles payment processing for approved insurance claims.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ClaimSwift Team")
                                .email("team@claimswift.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
