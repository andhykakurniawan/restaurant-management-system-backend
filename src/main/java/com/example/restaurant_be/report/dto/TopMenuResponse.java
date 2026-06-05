package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TopMenuResponse(
        UUID menuId,
        String menuName,
        Long quantitySold,
        BigDecimal revenue) {
}
