package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;

public record HourlySalesResponse(
        Integer hour,
        Long totalPayments,
        BigDecimal totalSales) {
}
