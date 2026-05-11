package com.example.restaurant_be.menuingredient.controller;

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
import com.example.restaurant_be.menuingredient.dto.MenuIngredientRequest;
import com.example.restaurant_be.menuingredient.dto.MenuIngredientResponse;
import com.example.restaurant_be.menuingredient.service.MenuIngredientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-ingredients")
@RequiredArgsConstructor
public class MenuIngredientController {
    private final MenuIngredientService menuIngredientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuIngredientResponse>>> findAll(){
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Ingredients retrieved successfully",
                        menuIngredientService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuIngredientResponse>> create(
            @RequestBody MenuIngredientRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Ingredient created successfully",
                        menuIngredientService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuIngredientResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Ingredient retrieved successfully",
                        menuIngredientService.findById(id)));
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponse<List<MenuIngredientResponse>>> findByMenuId(@PathVariable UUID menuId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Ingredients retrieved successfully",
                        menuIngredientService.findByMenuId(menuId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        menuIngredientService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu Ingredient deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MenuIngredientResponse>> restore(@PathVariable UUID id) {
        MenuIngredientResponse response = menuIngredientService.restore(id);
        return ResponseEntity.ok(
                ApiResponse.success("Menu Ingredient restored successfully", response));
    }
}
