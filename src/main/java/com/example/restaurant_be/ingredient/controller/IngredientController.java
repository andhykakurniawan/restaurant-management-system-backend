package com.example.restaurant_be.ingredient.controller;

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
import com.example.restaurant_be.ingredient.dto.IngredientRequest;
import com.example.restaurant_be.ingredient.dto.IngredientResponse;
import com.example.restaurant_be.ingredient.service.IngredientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ingredients retrieved successfully",
                        ingredientService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IngredientResponse>> create(
            @RequestBody @Valid IngredientRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ingredient created successfully",
                        ingredientService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ingredient retrieved successfully",
                ingredientService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid IngredientRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ingredient updated successfully",
                        ingredientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        ingredientService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<IngredientResponse>> restore(@PathVariable UUID id) {

        IngredientResponse response = ingredientService.restore(id);

        return ResponseEntity.ok(
                ApiResponse.success("Ingredient restored successfully", response));
    }
}
