package com.example.demo.workflow;

import com.example.demo.entity.ClaimStatus;

public class ClaimWorkflowValidator {

    public static boolean isValidTransition(
            ClaimStatus current,
            ClaimStatus next) {

        if (current == null || next == null)
            return false;

        switch (current) {

            case SUBMITTED:
                return next == ClaimStatus.UNDER_REVIEW;

            case UNDER_REVIEW:
                return next == ClaimStatus.APPROVED
                        || next == ClaimStatus.REJECTED;

            case APPROVED:
                return next == ClaimStatus.SETTLED;

            case REJECTED:
            case SETTLED:
                return false;   // Final states

            default:
                return false;
        }
    }
}