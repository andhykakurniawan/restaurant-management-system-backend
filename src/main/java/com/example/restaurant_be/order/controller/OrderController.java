package com.example.restaurant_be.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.restaurant_be.order.dto.OrderRequest;
import com.example.restaurant_be.order.dto.OrderResponse;
import com.example.restaurant_be.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<OrderResponse> restore(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.restore(id));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_WAITER')")
    public ResponseEntity<OrderResponse> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_WAITER')")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_WAITER')")
    public ResponseEntity<OrderResponse> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.completeOrder(id));
    }

    @PatchMapping("/{id}/preparing")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<OrderResponse> prepare(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.preparingOrder(id));
    }

    @PatchMapping("/{id}/ready")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_CHEF')")
    public ResponseEntity<OrderResponse> ready(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.readyOrder(id));
    }

    @PatchMapping("/{id}/served")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_WAITER')")
    public ResponseEntity<OrderResponse> served(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.servedOrder(id));
    }
}