package com.example.restaurant_be.payment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.restaurant_be.payment.dto.PaymentRequest;
import com.example.restaurant_be.payment.dto.PaymentResponse;
import com.example.restaurant_be.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public PaymentResponse findById(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return paymentService.create(request);
    }
}