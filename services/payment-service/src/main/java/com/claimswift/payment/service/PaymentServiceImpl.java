package com.claimswift.payment.service;

import com.claimswift.payment.client.ClaimClient;
import com.claimswift.payment.client.NotificationClient;
import com.claimswift.payment.dto.*;
import com.claimswift.payment.entity.*;
import com.claimswift.payment.exception.*;
import com.claimswift.payment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payment Service Implementation
 * 
 * Implements payment processing logic for approved claims
 * 
 * Integration Rules:
 * - Verifies claim status = APPROVED before processing payment
 * - Updates claim status to PAID after successful payment
 * - Triggers notification after payment completion
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final AuditPaymentRepository auditPaymentRepository;
    private final ClaimClient claimClient;
    private final NotificationClient notificationClient;

    private static final String CLAIM_STATUS_APPROVED = "APPROVED";
    private static final String CLAIM_STATUS_PAID = "PAID";
    private static final String NOTIFICATION_TYPE_PAYMENT_SUCCESS = "PAYMENT_SUCCESS";
    private static final String NOTIFICATION_TYPE_PAYMENT_FAILED = "PAYMENT_FAILED";

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for claim ID: {}", request.getClaimId());

        // Step 1: Verify claim exists and is approved
        ClaimResponse claim = verifyClaimStatus(request.getClaimId());

        // Step 2: Check if payment already exists for this claim
        paymentRepository.findByClaimId(request.getClaimId())
                .ifPresent(existing -> {
                    throw new PaymentAlreadyExistsException(
                            "Payment already exists for claim ID: " + request.getClaimId());
                });

        // Step 3: Create payment record
        Payment payment = Payment.builder()
                .claimId(request.getClaimId())
                .approvedAmount(request.getApprovedAmount())
                .paymentReference(generatePaymentReference())
                .status(PaymentStatus.INITIATED)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with ID: {} and reference: {}", 
                payment.getPaymentId(), payment.getPaymentReference());

        // Create audit record
        createAuditRecord(payment.getPaymentId(), "PAYMENT_INITIATED", null, 
                "Payment initiated", null);

        try {
            // Step 4: Process payment (simulate payment gateway)
            boolean paymentSuccess = processPaymentGateway(payment, request);

            if (paymentSuccess) {
                // Step 5: Update payment status to SUCCESS
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setProcessedAt(LocalDateTime.now());
                payment = paymentRepository.save(payment);

                // Step 6: Create transaction record
                createTransactionRecord(payment, true, null);

                // Step 7: Update claim status to PAID
                updateClaimStatus(payment.getClaimId(), CLAIM_STATUS_PAID);

                // Step 8: Send notification
                sendNotification(claim.getPolicyholderId(), payment.getClaimId(),
                        NOTIFICATION_TYPE_PAYMENT_SUCCESS,
                        String.format("Your payment of ₹%s has been processed successfully. " +
                                "Payment Reference: %s", 
                                payment.getApprovedAmount(), payment.getPaymentReference()));

                createAuditRecord(payment.getPaymentId(), "PAYMENT_SUCCESS", null,
                        "Payment processed successfully", null);

                log.info("Payment processed successfully for claim ID: {}", request.getClaimId());
            } else {
                // Payment failed
                handlePaymentFailure(payment, "Payment gateway rejected the transaction");
            }
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            handlePaymentFailure(payment, e.getMessage());
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with ID: " + paymentId));
        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByClaimId(Long claimId) {
        Payment payment = paymentRepository.findByClaimId(claimId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for claim ID: " + claimId));
        return mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse retryPayment(Long paymentId) {
        log.info("Retrying payment for payment ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with ID: " + paymentId));

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException(
                    "Only failed payments can be retried. Current status: " + payment.getStatus());
        }

        // Reset payment status
        payment.setStatus(PaymentStatus.INITIATED);
        payment = paymentRepository.save(payment);

        try {
            // Retry payment processing
            boolean success = processPaymentGateway(payment, null);

            if (success) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setProcessedAt(LocalDateTime.now());
                payment = paymentRepository.save(payment);

                // Update claim status
                updateClaimStatus(payment.getClaimId(), CLAIM_STATUS_PAID);

                log.info("Payment retry successful for payment ID: {}", paymentId);
            } else {
                handlePaymentFailure(payment, "Payment retry failed");
            }
        } catch (Exception e) {
            handlePaymentFailure(payment, e.getMessage());
        }

        return mapToPaymentResponse(payment);
    }

    private ClaimResponse verifyClaimStatus(Long claimId) {
        log.info("Verifying claim status for claim ID: {}", claimId);

        try {
            var response = claimClient.getClaim(claimId);

            if (!response.getStatusCode().is2xxSuccessful() || 
                    response.getBody() == null || 
                    !response.getBody().isSuccess()) {
                throw new ClaimNotFoundException("Claim not found or inaccessible: " + claimId);
            }

            ClaimResponse claim = response.getBody().getData();
            
            if (!CLAIM_STATUS_APPROVED.equals(claim.getStatus())) {
                throw new InvalidClaimStatusException(
                        "Claim status must be APPROVED for payment. Current status: " + claim.getStatus());
            }

            log.info("Claim verified - ID: {}, Status: {}", claimId, claim.getStatus());
            return claim;

        } catch (Exception e) {
            log.error("Error calling claim service: {}", e.getMessage());
            throw new ServiceCommunicationException(
                    "Failed to communicate with Claim Service", e);
        }
    }

    private void updateClaimStatus(Long claimId, String newStatus) {
        log.info("Updating claim status to {} for claim ID: {}", newStatus, claimId);

        try {
            ClaimStatusUpdateRequest request = ClaimStatusUpdateRequest.builder()
                    .newStatus(newStatus)
                    .build();

            var response = claimClient.updateClaimStatus(claimId, request);

            if (!response.getStatusCode().is2xxSuccessful() || 
                    response.getBody() == null || 
                    !response.getBody().isSuccess()) {
                throw new ServiceCommunicationException(
                        "Failed to update claim status in Claim Service");
            }

            log.info("Claim status updated successfully to {}", newStatus);

        } catch (Exception e) {
            log.error("Error updating claim status: {}", e.getMessage());
            // Compensation logic: Log for retry
            throw new ServiceCommunicationException(
                    "Failed to update claim status. Manual intervention may be required.", e);
        }
    }

    private boolean processPaymentGateway(Payment payment, PaymentRequest request) {
        // Simulate payment gateway processing
        // In production, integrate with actual payment gateway (Razorpay, Stripe, etc.)
        log.info("Processing payment gateway for payment ID: {}", payment.getPaymentId());

        // Simulate success (90% success rate for demo)
        return Math.random() > 0.1;
    }

    private void createTransactionRecord(Payment payment, boolean success, String failureReason) {
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .bankReference(generateBankReference())
                .transactionStatus(success ? TransactionStatus.COMPLETED : TransactionStatus.FAILED)
                .failureReason(failureReason)
                .transactionTime(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction record created for payment ID: {}", payment.getPaymentId());
    }

    private void handlePaymentFailure(Payment payment, String reason) {
        log.error("Payment failed for payment ID: {}, Reason: {}", payment.getPaymentId(), reason);

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        createTransactionRecord(payment, false, reason);

        createAuditRecord(payment.getPaymentId(), "PAYMENT_FAILED", null, reason, null);

        // Send failure notification
        try {
            claimClient.getClaim(payment.getClaimId()).getBody().getData();
            // Get user ID and send notification
        } catch (Exception e) {
            log.warn("Could not send failure notification: {}", e.getMessage());
        }
    }

    private void sendNotification(Long userId, Long claimId, String type, String message) {
        try {
            NotificationRequest request = NotificationRequest.builder()
                    .userId(userId)
                    .claimId(claimId)
                    .type(type)
                    .message(message)
                    .build();

            notificationClient.sendNotification(request);
            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.warn("Failed to send notification: {}", e.getMessage());
            // Non-blocking - don't fail payment due to notification failure
        }
    }

    private void createAuditRecord(Long paymentId, String action, Long performedBy,
                                    String description, String previousValue) {
        AuditPayment audit = AuditPayment.builder()
                .paymentId(paymentId)
                .action(action)
                .performedBy(performedBy)
                .description(description)
                .previousValue(previousValue)
                .timestamp(LocalDateTime.now())
                .build();

        auditPaymentRepository.save(audit);
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateBankReference() {
        return "TXN-" + System.currentTimeMillis();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse.PaymentResponseBuilder builder = PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .claimId(payment.getClaimId())
                .approvedAmount(payment.getApprovedAmount())
                .paymentReference(payment.getPaymentReference())
                .status(payment.getStatus())
                .processedAt(payment.getProcessedAt())
                .createdAt(payment.getCreatedAt());

        // Fetch transaction if exists
        transactionRepository.findByPaymentPaymentId(payment.getPaymentId())
                .ifPresent(transaction -> builder.transaction(
                        PaymentResponse.TransactionResponse.builder()
                                .transactionId(transaction.getTransactionId())
                                .bankReference(transaction.getBankReference())
                                .transactionStatus(transaction.getTransactionStatus().name())
                                .transactionTime(transaction.getTransactionTime())
                                .failureReason(transaction.getFailureReason())
                                .build()));

        return builder.build();
    }
}
