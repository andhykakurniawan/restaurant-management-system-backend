package com.example.restaurant_be.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restaurant_be.payment.entity.Payment;
import com.example.restaurant_be.payment.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByOrder_IdAndStatus(UUID orderId, PaymentStatus status);
}
