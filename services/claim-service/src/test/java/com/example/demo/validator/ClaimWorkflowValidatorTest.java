package com.example.demo.validator;

import org.junit.jupiter.api.Test;

import com.example.demo.entity.ClaimStatus;
import com.example.demo.workflow.ClaimWorkflowValidator;

import static org.junit.jupiter.api.Assertions.*;

class ClaimWorkflowValidatorTest {

    @Test
    void validTransition_SubmittedToUnderReview() {

        boolean result =
                ClaimWorkflowValidator.isValidTransition(
                        ClaimStatus.SUBMITTED,
                        ClaimStatus.UNDER_REVIEW);

        assertTrue(result);
    }

    @Test
    void invalidTransition_SubmittedToApproved() {

        boolean result =
                ClaimWorkflowValidator.isValidTransition(
                        ClaimStatus.SUBMITTED,
                        ClaimStatus.APPROVED);

        assertFalse(result);
    }
}