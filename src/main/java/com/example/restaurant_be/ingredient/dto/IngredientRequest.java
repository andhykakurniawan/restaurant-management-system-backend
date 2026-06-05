package com.example.restaurant_be.ingredient.dto;

import java.math.BigDecimal;

import com.example.restaurant_be.ingredient.entity.Unit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientRequest(

        @NotBlank
        String name,

        @NotNull
        Unit unit,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal currentstock,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal minimumstock,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal costperunit
) {}
