package com.example.restaurant_be.ordersession.dto;

import java.util.UUID;

import com.example.restaurant_be.ordersession.entity.SessionStatus;

public record OrderSessionResponse(
    UUID id,
    UUID tableId,
    String tableNumber,
    UUID createdBy,
    String userName,
    String sessionToken,
    String sessionUrl,
    String qrCode,
    SessionStatus status,
    Boolean isOpen,
    String startTime,
    String endTime
) {
}