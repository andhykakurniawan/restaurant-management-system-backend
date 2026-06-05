package com.example.restaurant_be.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restaurant_be.payment.entity.PaymentBackLog;

public interface PaymentBackLogRepository extends JpaRepository<PaymentBackLog, UUID> {

    boolean existsByTransactionIdAndTransactionStatus(String transactionId, String transactionStatus);
}
