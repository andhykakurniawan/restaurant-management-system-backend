package com.example.restaurant_be.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.restaurant_be.order.dto.OrderItemRequest;
import com.example.restaurant_be.order.dto.OrderItemResponse;
import com.example.restaurant_be.order.service.OrderItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> findAll(
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(orderItemService.findAll(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderItemRequest request) {

        return ResponseEntity.ok(orderItemService.create(orderId, request));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<OrderItemResponse> update(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @Valid @RequestBody OrderItemRequest request) {

        return ResponseEntity.ok(orderItemService.update(orderId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId) {

        orderItemService.delete(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
