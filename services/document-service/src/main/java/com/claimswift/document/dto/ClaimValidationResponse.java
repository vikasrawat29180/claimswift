package com.claimswift.document.dto;

import lombok.Data;

@Data
public class ClaimValidationResponse {
    private boolean valid;
    private String status;
}