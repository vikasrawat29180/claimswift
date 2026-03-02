package com.claimswift.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthValidationResponse {
    private boolean valid;
    private String username;
    private List<String> roles;
}