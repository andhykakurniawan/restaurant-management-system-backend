package com.example.restaurant_be.shift.dto;

import java.util.UUID;

public record ShiftResponse(
        UUID id,
        String shiftName,
        String startTime,
        String endTime,
        Integer graceMinutes,
        boolean isActive) {
}
