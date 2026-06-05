package com.example.restaurant_be.ordersession.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record OrderSessionRequest(
    @NotNull
    UUID tableId,

    UUID bookingId
) {
}
