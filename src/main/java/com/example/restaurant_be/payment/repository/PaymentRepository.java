package com.example.restaurant_be.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restaurant_be.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}