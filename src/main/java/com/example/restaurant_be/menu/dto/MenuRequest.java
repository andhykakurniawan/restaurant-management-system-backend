package com.example.restaurant_be.menu.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuRequest(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal price,

        @NotNull
        String imageUrl,

        @NotNull
        Boolean isAvailable,

        @NotNull
        UUID categoryId
) {}
