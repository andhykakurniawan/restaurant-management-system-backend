package com.example.restaurant_be.inventory.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.inventory.dto.LowStockIngredientResponse;
import com.example.restaurant_be.inventory.dto.StockAdjustmentRequest;
import com.example.restaurant_be.inventory.dto.StockMovementResponse;
import com.example.restaurant_be.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock-movements")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> findAllMovements() {
        return ResponseEntity.ok(ApiResponse.success(
                "Stock movements retrieved successfully",
                inventoryService.findAllMovements()));
    }

    @GetMapping("/ingredients/{ingredientId}/stock-movements")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> findMovementsByIngredient(
            @PathVariable UUID ingredientId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Ingredient stock movements retrieved successfully",
                inventoryService.findMovementsByIngredient(ingredientId)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockIngredientResponse>>> findLowStockIngredients() {
        return ResponseEntity.ok(ApiResponse.success(
                "Low stock ingredients retrieved successfully",
                inventoryService.findLowStockIngredients()));
    }

    @PatchMapping("/ingredients/{ingredientId}/stock")
    public ResponseEntity<ApiResponse<StockMovementResponse>> adjustStock(
            @PathVariable UUID ingredientId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Ingredient stock adjusted successfully",
                inventoryService.adjustStock(ingredientId, request)));
    }
}
