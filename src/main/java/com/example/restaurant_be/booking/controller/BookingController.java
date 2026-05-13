package com.example.restaurant_be.booking.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.booking.dto.BookingRequest;
import com.example.restaurant_be.booking.dto.BookingResponse;
import com.example.restaurant_be.booking.service.BookingPaymentService;
import com.example.restaurant_be.booking.service.BookingService;
import com.example.restaurant_be.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingPaymentService bookingPaymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Bookings retrieved successfully",
                        bookingService.getAllBookings()));
    }

    @GetMapping("/{bookingCode}")
    public ResponseEntity<ApiResponse<BookingResponse>> findByCode(@PathVariable String bookingCode) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Booking retrieved successfully",
                        bookingService.getBookingByCode(bookingCode)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Booking created successfully",
                        bookingService.create(request)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/payment/notification")
    public ResponseEntity<Void> handleNotification(
            @RequestBody Map<String, Object> payload) {

        bookingPaymentService.handleNotification(
                payload);

        return ResponseEntity.ok().build();
    }

}
