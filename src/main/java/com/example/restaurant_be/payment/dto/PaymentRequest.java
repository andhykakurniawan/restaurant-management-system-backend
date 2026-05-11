package com.example.restaurant_be.payment.dto;

import java.util.UUID;
import com.example.restaurant_be.payment.entity.PaymentMethod;

public record PaymentRequest (
    UUID orderId,
    PaymentMethod paymentMethod 
 ) {}
