package com.claimswift.payment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity Unit Tests
 * 
 * Tests for JPA entities
 */
class EntityTest {

    // =============================================
    // TEST: Payment Entity
    // =============================================
    @Test
    @DisplayName("Payment Entity - Builder and Getters")
    void paymentEntity_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        Payment payment = Payment.builder()
                .paymentId(1L)
                .claimId(100L)
                .approvedAmount(new BigDecimal("50000.00"))
                .paymentReference("PAY-ABC123")
                .status(PaymentStatus.INITIATED)
                .createdAt(now)
                .build();

        assertEquals(1L, payment.getPaymentId());
        assertEquals(100L, payment.getClaimId());
        assertEquals(new BigDecimal("50000.00"), payment.getApprovedAmount());
        assertEquals("PAY-ABC123", payment.getPaymentReference());
        assertEquals(PaymentStatus.INITIATED, payment.getStatus());
    }

    @Test
    @DisplayName("Payment Entity - PrePersist Sets Default Status")
    void paymentEntity_PrePersistSetsDefaultStatus() {
        Payment payment = new Payment();
        payment.setClaimId(1L);
        payment.setApprovedAmount(new BigDecimal("10000.00"));
        
        // Simulate pre-persist
        payment.onCreate();

        assertNotNull(payment.getCreatedAt());
        assertEquals(PaymentStatus.INITIATED, payment.getStatus());
    }

    @Test
    @DisplayName("Payment Entity - PreUpdate Updates Timestamp")
    void paymentEntity_PreUpdateUpdatesTimestamp() {
        Payment payment = Payment.builder()
                .claimId(1L)
                .approvedAmount(new BigDecimal("10000.00"))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Simulate pre-update
        payment.onUpdate();

        assertNotNull(payment.getUpdatedAt());
    }

    // =============================================
    // TEST: Transaction Entity
    // =============================================
    @Test
    @DisplayName("Transaction Entity - Builder and Getters")
    void transactionEntity_BuilderAndGetters() {
        Payment payment = Payment.builder()
                .paymentId(1L)
                .build();

        Transaction transaction = Transaction.builder()
                .transactionId(1L)
                .payment(payment)
                .bankReference("TXN-123456")
                .transactionStatus(TransactionStatus.COMPLETED)
                .build();

        assertEquals(1L, transaction.getTransactionId());
        assertEquals("TXN-123456", transaction.getBankReference());
        assertEquals(TransactionStatus.COMPLETED, transaction.getTransactionStatus());
    }

    @Test
    @DisplayName("Transaction Entity - PrePersist Sets Default Time")
    void transactionEntity_PrePersistSetsDefaultTime() {
        Transaction transaction = new Transaction();
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        
        // Simulate pre-persist
        transaction.onCreate();

        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getTransactionTime());
    }

    // =============================================
    // TEST: AuditPayment Entity
    // =============================================
    @Test
    @DisplayName("AuditPayment Entity - Builder and Getters")
    void auditPaymentEntity_BuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        
        AuditPayment audit = AuditPayment.builder()
                .id(1L)
                .paymentId(100L)
                .action("PAYMENT_INITIATED")
                .performedBy(5L)
                .description("Payment initiated successfully")
                .previousValue("INITIATED")
                .newValue("SUCCESS")
                .timestamp(now)
                .build();

        assertEquals(1L, audit.getId());
        assertEquals(100L, audit.getPaymentId());
        assertEquals("PAYMENT_INITIATED", audit.getAction());
        assertEquals(5L, audit.getPerformedBy());
        assertEquals("INITIATED", audit.getPreviousValue());
        assertEquals("SUCCESS", audit.getNewValue());
    }

    @Test
    @DisplayName("AuditPayment Entity - PrePersist Sets Default Timestamp")
    void auditPaymentEntity_PrePersistSetsDefaultTimestamp() {
        AuditPayment audit = new AuditPayment();
        audit.setPaymentId(1L);
        audit.setAction("TEST_ACTION");
        
        // Simulate pre-persist
        audit.onCreate();

        assertNotNull(audit.getTimestamp());
    }

    // =============================================
    // TEST: Payment Status Enum
    // =============================================
    @Test
    @DisplayName("Payment Status Enum - Values")
    void paymentStatusEnum_Values() {
        PaymentStatus[] statuses = PaymentStatus.values();
        
        assertEquals(3, statuses.length);
        assertEquals(PaymentStatus.INITIATED, PaymentStatus.valueOf("INITIATED"));
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
    }

    // =============================================
    // TEST: Transaction Status Enum
    // =============================================
    @Test
    @DisplayName("Transaction Status Enum - Values")
    void transactionStatusEnum_Values() {
        TransactionStatus[] statuses = TransactionStatus.values();
        
        assertEquals(3, statuses.length);
        assertEquals(TransactionStatus.PENDING, TransactionStatus.valueOf("PENDING"));
        assertEquals(TransactionStatus.COMPLETED, TransactionStatus.valueOf("COMPLETED"));
        assertEquals(TransactionStatus.FAILED, TransactionStatus.valueOf("FAILED"));
    }
}
