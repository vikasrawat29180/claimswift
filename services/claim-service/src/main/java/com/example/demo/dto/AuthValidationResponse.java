package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthValidationResponse {
    private boolean valid;
    private String username;
    private List<String> roles;
}
