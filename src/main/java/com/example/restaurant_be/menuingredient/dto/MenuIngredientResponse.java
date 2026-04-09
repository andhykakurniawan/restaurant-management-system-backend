package com.example.restaurant_be.menuingredient.dto;

import java.util.UUID;

public record MenuIngredientResponse(
        UUID id,
        UUID menuId,
        String menuName,
        UUID ingredientId,
        String ingredientName,
        Integer quantity,
        boolean is_active
) {}
