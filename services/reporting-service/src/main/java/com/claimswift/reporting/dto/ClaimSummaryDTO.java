package com.claimswift.reporting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimSummaryDTO {

    private Long total;
    private Long approved;
    private Long rejected;
    private Double avgSettlementTime;
}