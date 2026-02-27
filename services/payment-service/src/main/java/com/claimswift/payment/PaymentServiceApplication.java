package com.claimswift.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Payment Service Application
 * 
 * Main entry point for the Payment Service
 * 
 * This service handles payment processing for approved insurance claims
 * 
 * Integration Points:
 * - Claim Service: To verify claim status and update to PAID
 * - Notification Service: To send payment notifications
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.claimswift.payment.client")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
