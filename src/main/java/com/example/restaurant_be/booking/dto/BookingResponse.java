package com.example.restaurant_be.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record BookingResponse (
    UUID id,
    String bookingCode,
    String customerName,
    String customerPhone,
    Integer numberPerson,
    UUID tableId,
    String tableNumber,
    LocalDate bookingDate,
    LocalTime bookingTime,
    BigDecimal dpAmount,
    String snapToken,
    String status
) {
    
}
