package com.example.restaurant_be.category.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.example.restaurant_be.category.dto.CategoryRequest;
import com.example.restaurant_be.category.dto.CategoryResponse;
import com.example.restaurant_be.category.entity.Category;
import com.example.restaurant_be.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIsActive());
    }

    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }

    public CategoryResponse findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return toResponse(category);
    }

    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        categoryRepository.delete(category);
    }

    public CategoryResponse restore(UUID id) {

        Category category = categoryRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (Boolean.TRUE.equals(category.getIsActive())) {
            throw new IllegalArgumentException("Category already active");
        }

        category.setIsActive(true);
        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }
}
