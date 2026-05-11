package com.example.restaurant_be.menucategory.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record MenuCategoryRequest(
        @NotNull UUID menuId,

        @NotNull UUID categoryId,

        @NotNull @DecimalMin("0.0")
        BigDecimal price) {
}