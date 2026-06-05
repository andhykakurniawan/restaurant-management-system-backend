package com.example.restaurant_be.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.restaurant_be.order.entity.Status;

public record OrderResponse(
    UUID id,
    String orderCode,
    UUID sessionId,
    UUID tableId,
    String tableNumber,
    UUID createdById,
    String createdByName,
    Status status,
    BigDecimal totalAmount
) {}
