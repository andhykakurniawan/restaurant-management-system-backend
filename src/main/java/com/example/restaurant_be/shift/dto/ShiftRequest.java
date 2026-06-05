package com.example.restaurant_be.shift.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ShiftRequest(
        @NotBlank
        String shiftName,

        @NotBlank
        String startTime,

        @NotBlank
        String endTime,

        @NotNull
        @PositiveOrZero
        Integer graceMinutes) {
}
