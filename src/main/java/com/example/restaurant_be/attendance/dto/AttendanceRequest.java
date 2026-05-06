package com.example.restaurant_be.attendance.dto;

import java.util.UUID;
import com.example.restaurant_be.attendance.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotNull UUID shiftId,
        String attendanceDate,
        String clockIn,
        String clockOut,
        @NotBlank Status status,
        String notes) {
}