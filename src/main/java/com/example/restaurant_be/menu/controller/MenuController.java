package com.example.restaurant_be.menu.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.menu.dto.MenuRequest;
import com.example.restaurant_be.menu.dto.MenuResponse;
import com.example.restaurant_be.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menus retrieved successfully",
                        menuService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> create(
            @RequestBody @Valid MenuRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu created successfully",
                        menuService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu retrieved successfully",
                menuService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid MenuRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu updated successfully",
                        menuService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        menuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Menu deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MenuResponse>> restore(@PathVariable UUID id) {

        MenuResponse response = menuService.restore(id);

        return ResponseEntity.ok(
                ApiResponse.success("Menu restored successfully", response));
    }
}
