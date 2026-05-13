package com.example.restaurant_be.booking.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
    @NotBlank(message = "Customer name is required")
    String customerName,

    @NotBlank(message = "Customer phone is required")
    String customerPhone,

    @NotNull(message = "Number of person is required")
    Integer numberPerson,

    @NotNull
    UUID tableId,

    @NotBlank
    String bookingDate,

    @NotBlank
    String bookingTime,

    @NotNull
    @DecimalMin("0.0")
    BigDecimal dpAmount
) {
} 
