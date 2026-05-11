package com.example.restaurant_be.shift.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.shift.dto.ShiftRequest;
import com.example.restaurant_be.shift.dto.ShiftResponse;
import com.example.restaurant_be.shift.service.ShiftService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Shifts retrieved successfully",
                        shiftService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponse>> create(
            @RequestBody ShiftRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Shift created successfully",
                        shiftService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu retrieved successfully",
                        shiftService.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        shiftService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Menu deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<ShiftResponse>> restore(@PathVariable UUID id) {

        ShiftResponse response = shiftService.restore(id);

        return ResponseEntity.ok(
                ApiResponse.success("Menu restored successfully", response));
    }
}
