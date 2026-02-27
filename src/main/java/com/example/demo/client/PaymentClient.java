package com.example.demo.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient webClient;

    @Value("${payment.base-url}")
    private String paymentBaseUrl;

    public void initiatePayment(Long claimId, Double amount) {

        PaymentRequest request = new PaymentRequest(claimId, amount);

        webClient.post()
                .uri(paymentBaseUrl + "/api/v1/payments/initiate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public record PaymentRequest(Long claimId, Double amount) {}
}