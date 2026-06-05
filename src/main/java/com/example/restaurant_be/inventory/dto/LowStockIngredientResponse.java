package com.example.restaurant_be.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.restaurant_be.ingredient.entity.Unit;

public record LowStockIngredientResponse(
        UUID id,
        String name,
        Unit unit,
        BigDecimal currentStock,
        BigDecimal minimumStock) {
}
