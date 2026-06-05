package com.example.restaurant_be.ordersession.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.ordersession.dto.OrderSessionRequest;
import com.example.restaurant_be.ordersession.dto.OrderSessionResponse;
import com.example.restaurant_be.ordersession.service.OrderSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order-sessions")
@RequiredArgsConstructor
public class OrderSessionController {

    private final OrderSessionService orderSessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSessionResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order sessions retrieved successfully",
                        orderSessionService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('WAITER')")
    public ResponseEntity<ApiResponse<OrderSessionResponse>> createSession(
            @Valid @RequestBody OrderSessionRequest request) {

        OrderSessionResponse response = orderSessionService.createSession(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order session created successfully",
                        response));
    }

    @PostMapping("/walk-in")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('WAITER')")
    public ResponseEntity<ApiResponse<OrderSessionResponse>> createWalkInSession(
            @Valid @RequestBody OrderSessionRequest request) {

        OrderSessionResponse response = orderSessionService.createWalkInSession(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Walk-in order session created successfully",
                        response));
    }

    @PostMapping("/check-in-booking")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('WAITER')")
    public ResponseEntity<ApiResponse<OrderSessionResponse>> checkInBooking(
            @Valid @RequestBody OrderSessionRequest request) {

        OrderSessionResponse response = orderSessionService.checkInBooking(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Booking checked in successfully",
                        response));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('WAITER')")
    public ResponseEntity<ApiResponse<UUID>> closeSession(@PathVariable("id") UUID id) {
        orderSessionService.closeSession(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order session closed successfully",
                        null));
    }
}
