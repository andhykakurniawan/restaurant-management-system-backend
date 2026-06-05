package com.example.restaurant_be.category.controller;

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

import com.example.restaurant_be.category.dto.CategoryRequest;
import com.example.restaurant_be.category.dto.CategoryResponse;
import com.example.restaurant_be.category.service.CategoryService;
import com.example.restaurant_be.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor

public class CategoryController {
        private final CategoryService categoryService;

        @GetMapping
        public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Categories retrieved successfully",
                                                categoryService.findAll()));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<CategoryResponse>> create(
                        @RequestBody @Valid CategoryRequest request) {
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Category created successfully",
                                                categoryService.create(request)));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable UUID id) {
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Category retrieved successfully",
                                                categoryService.findById(id)));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponse>> update(
                        @PathVariable UUID id,
                        @RequestBody @Valid CategoryRequest request) {
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Category updated successfully",
                                                categoryService.update(id, request)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
                categoryService.delete(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Category deleted successfully", null));
        }

        @PatchMapping("/{id}/restore")
        public ResponseEntity<ApiResponse<CategoryResponse>> restore(@PathVariable UUID id) {

                CategoryResponse response = categoryService.restore(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Category restored successfully", response));
        }
}
