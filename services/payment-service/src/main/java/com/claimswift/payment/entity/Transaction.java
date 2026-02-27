package com.claimswift.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Transaction Entity - Records payment transaction details
 * 
 * Database: payment_db
 * Table: transactions
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "bank_reference", length = 100)
    private String bankReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 20)
    private TransactionStatus transactionStatus;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
    }
}
