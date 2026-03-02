package com.claimswift.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String message;
    private String username;
    private List<String> roles;
    private String token;
    
    public AuthResponse(String token) {
        this.token = token;
    }
}