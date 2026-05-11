package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;

public record YearlySalesReportResponse(
        Integer year,
        Long totalOrders,
        Long totalItemsSold,
        BigDecimal grossRevenue
) {}