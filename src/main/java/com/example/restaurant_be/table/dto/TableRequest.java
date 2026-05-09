package com.example.restaurant_be.table.dto;

import com.example.restaurant_be.table.entity.AllocationType;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.entity.TableArea;

import jakarta.validation.constraints.NotBlank;

public record TableRequest(
        @NotBlank 
        String tableNumber,

        @NotBlank 
        Integer capacity,

        @NotBlank 
        AllocationType allocationType,

        @NotBlank 
        TableStatus status,

        @NotBlank
        TableArea area
    ) {}
