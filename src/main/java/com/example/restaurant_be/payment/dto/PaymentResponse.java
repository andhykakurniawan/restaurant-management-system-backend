package com.example.restaurant_be.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    String method,
    String status
) {}
