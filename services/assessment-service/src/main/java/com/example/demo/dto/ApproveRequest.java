package com.example.demo.dto;

public record ApproveRequest(
        Long claimId,
        Double approvedAmount
) {}