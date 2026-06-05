package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SalesByCashierResponse(
        UUID cashierId,
        String cashierName,
        Long totalPayments,
        BigDecimal totalSales) {
}
