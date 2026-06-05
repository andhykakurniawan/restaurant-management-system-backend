package com.example.restaurant_be.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.restaurant_be.inventory.entity.StockMovementType;

public record StockMovementResponse(
        UUID id,
        UUID ingredientId,
        String ingredientName,
        UUID orderId,
        StockMovementType type,
        BigDecimal quantity,
        BigDecimal stockBefore,
        BigDecimal stockAfter,
        String note) {
}
