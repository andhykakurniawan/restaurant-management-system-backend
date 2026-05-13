package com.example.restaurant_be.ordersession.dto;

import java.util.UUID;

public record OrderSessionRequest(
    UUID tableId,
    UUID bookingId
) {
}