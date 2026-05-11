package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record DailySalesReportResponse(
        LocalDate date,
        Long totalOrders,
        Long totalItemsSold,
        BigDecimal grossRevenue,
        Map<String, BigDecimal> payments
) {}