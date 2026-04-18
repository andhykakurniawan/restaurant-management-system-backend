package com.example.restaurant_be.menucategory.controller;

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
import com.example.restaurant_be.menucategory.dto.MenuCategoryRequest;
import com.example.restaurant_be.menucategory.dto.MenuCategoryResponse;
import com.example.restaurant_be.menucategory.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
public class MenuCategoryController {
    private final MenuCategoryService menuCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuCategoryResponse>>> findAll(){
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Categorys retrieved successfully",
                        menuCategoryService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> create(
            @RequestBody MenuCategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Category created successfully",
                        menuCategoryService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Category retrieved successfully",
                        menuCategoryService.findById(id)));
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponse<List<MenuCategoryResponse>>> findByMenuId(@PathVariable UUID menuId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Categorys retrieved successfully",
                        menuCategoryService.findByMenuId(menuId)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<MenuCategoryResponse>>> findByCategoryId(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Menu Categorys retrieved successfully",
                        menuCategoryService.findByCategoryId(categoryId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        menuCategoryService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu Category deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> restore(@PathVariable UUID id) {
        MenuCategoryResponse response = menuCategoryService.restore(id);
        return ResponseEntity.ok(
                ApiResponse.success("Menu Category restored successfully", response));
    }
}
