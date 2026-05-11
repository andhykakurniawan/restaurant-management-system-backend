package com.example.restaurant_be.report.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public record MonthlySalesReportResponse(
        YearMonth month,
        Long totalOrders,
        Long totalItemsSold,
        BigDecimal grossRevenue,
        Map<String, BigDecimal> payments
) {}