package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponseDTO {

    private Long claimId;
    private String policyNumber;
    private String claimType;
    private Double claimAmount;
    private String status;
}