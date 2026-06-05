package com.example.restaurant_be.inventory.dto;

import java.math.BigDecimal;

import com.example.restaurant_be.inventory.entity.StockMovementType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockAdjustmentRequest(
        @NotNull
        StockMovementType type,

        @NotNull
        @Positive
        BigDecimal quantity,

        String note) {
}
