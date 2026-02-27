package com.example.demo.dto;


import lombok.Data;

@Data
public class ClaimDto {

    private String policyNumber;
    private Double amount;
    private String description;
}