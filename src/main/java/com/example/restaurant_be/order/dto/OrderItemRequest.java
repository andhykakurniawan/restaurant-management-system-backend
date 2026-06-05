package com.example.restaurant_be.order.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest( 
        @NotNull
        UUID menuId,

        @NotNull
        @Positive
        Integer quantity
) {}
