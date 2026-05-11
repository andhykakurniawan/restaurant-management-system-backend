package com.example.restaurant_be.menuingredient.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record MenuIngredientRequest(
        @NotNull UUID menuId,
        @NotNull UUID ingredientId,
        Integer quantity) {
}
