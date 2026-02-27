package com.claimswift.payment.repository;

import com.claimswift.payment.entity.Payment;
import com.claimswift.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Payment Repository
 * 
 * JPA Repository for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByClaimId(Long claimId);

    Optional<Payment> findByPaymentReference(String paymentReference);

    boolean existsByClaimIdAndStatus(Long claimId, PaymentStatus status);
}
