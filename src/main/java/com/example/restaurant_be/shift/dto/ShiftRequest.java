package com.example.restaurant_be.shift.dto;

public record ShiftRequest(
        String shiftName,
        String startTime,
        String endTime,
        Integer graceMinutes) {
}