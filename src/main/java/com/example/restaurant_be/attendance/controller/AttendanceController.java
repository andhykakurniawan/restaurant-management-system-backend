package com.example.restaurant_be.attendance.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.attendance.dto.AttendanceRequest;
import com.example.restaurant_be.attendance.dto.AttendanceResponse;
import com.example.restaurant_be.attendance.service.AttendanceService;
import com.example.restaurant_be.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService; 

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @RequestBody AttendanceRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check-in success",
                        attendanceService.checkIn(request)));
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check-out success",
                        attendanceService.checkOut(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance retrieved successfully",
                        attendanceService.findById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> findByUser(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendances retrieved successfully",
                        attendanceService.findByUser(userId)));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> findToday() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Today's attendances retrieved successfully",
                        attendanceService.findToday()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> findAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All attendances retrieved successfully",
                        attendanceService.findAll()));
    }
}
