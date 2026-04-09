package com.example.restaurant_be.report.projection;

import java.math.BigDecimal;

public interface SummaryProjection {

    Long getTotalOrders();
    BigDecimal getGrossRevenue();

}