package com.example.restaurant_be.menucategory.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuCategoryResponse(
        UUID id,
        UUID menuId,
        String menuName,
        UUID categoryId,
        String categoryName,
        BigDecimal price,
        boolean is_active) {
}
