package com.example.restaurant_be.payment.dto;

import java.util.UUID;
import com.example.restaurant_be.payment.entity.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest (
    @NotNull
    UUID orderId,

    @NotNull
    PaymentMethod paymentMethod 
 ) {}
