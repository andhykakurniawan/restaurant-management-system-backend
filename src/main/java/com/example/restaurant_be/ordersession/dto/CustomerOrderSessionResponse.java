package com.example.restaurant_be.ordersession.dto;

import java.util.UUID;

import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.ordersession.entity.SessionStatus;

public record CustomerOrderSessionResponse(
        UUID sessionId,
        String tableNumber,
        String sessionToken,
        SessionStatus sessionStatus,
        UUID orderId,
        Status orderStatus) {
}
