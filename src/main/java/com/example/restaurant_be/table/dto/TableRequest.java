package com.example.restaurant_be.table.dto;

import com.example.restaurant_be.table.entity.AllocationType;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.entity.TableArea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TableRequest(
        @NotBlank 
        String tableNumber,

        @NotNull
        @Positive
        Integer capacity,

        @NotNull
        AllocationType allocationType,

        @NotNull
        TableStatus status,

        @NotNull
        TableArea area
    ) {}
