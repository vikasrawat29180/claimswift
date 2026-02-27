package com.claimswift.reporting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSummaryDTO {

    private Long totalPayments = 0L;
    private Double totalAmountSettled = 0.0;
    private Double avgSettlementProcessingTime = 0.0;
}