package com.example.restaurant_be.menuingredient.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MenuIngredientRequest(
        @NotNull UUID menuId,
        @NotNull UUID ingredientId,
        @NotNull
        @Positive
        Integer quantity) {
}
