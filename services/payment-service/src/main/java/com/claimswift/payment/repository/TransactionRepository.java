package com.claimswift.payment.repository;

import com.claimswift.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Transaction Repository
 * 
 * JPA Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPaymentPaymentId(Long paymentId);

    Optional<Transaction> findByBankReference(String bankReference);
}
