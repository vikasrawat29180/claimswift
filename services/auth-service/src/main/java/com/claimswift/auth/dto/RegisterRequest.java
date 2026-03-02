package com.claimswift.auth.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    // Initialize roles to empty set to avoid NullPointerException
    private Set<String> roles = new HashSet<>();
}