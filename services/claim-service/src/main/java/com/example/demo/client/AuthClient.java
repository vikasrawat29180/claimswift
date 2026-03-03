package com.example.demo.client;

import com.example.demo.dto.AuthValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate;

    public AuthValidationResponse validate(String token){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<AuthValidationResponse> res =
                restTemplate.exchange(
                        "http://localhost:8081/auth/validate",
                        HttpMethod.POST,
                        entity,
                        AuthValidationResponse.class
                );

        return res.getBody();
    }
}