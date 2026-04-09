package com.example.restaurant_be.report.projection;

import java.math.BigDecimal;

public interface PaymentMethodProjection {
    String getMethod();
    BigDecimal getTotal();
}
