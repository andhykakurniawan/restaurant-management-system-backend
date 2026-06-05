package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueAggregationResponse(
        LocalDate period,
        Long totalPayments,
        BigDecimal totalSales) {
}
