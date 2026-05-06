package com.example.restaurant_be.attendance.dto;

import java.util.UUID;

import com.example.restaurant_be.attendance.entity.Status;

public record AttendanceResponse(
        UUID id,
        UUID userId,
        String userName,
        UUID shiftId,
        String shiftName,
        String clockIn,
        String clockOut,
        Status status,
        String notes) {
}
