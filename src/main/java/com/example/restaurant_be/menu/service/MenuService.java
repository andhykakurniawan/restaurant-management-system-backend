package com.example.restaurant_be.menu.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.category.entity.Category;
import com.example.restaurant_be.category.repository.CategoryRepository;
import com.example.restaurant_be.menu.dto.MenuRequest;
import com.example.restaurant_be.menu.dto.MenuResponse;
import com.example.restaurant_be.menu.entity.Menu;
import com.example.restaurant_be.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public List<MenuResponse> findAll() {
        return menuRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MenuResponse toResponse(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getCategory().getId(),
                menu.getCategory().getName(),
                menu.getImageUrl(),
                menu.getIsAvailable(),
                menu.getIsActive());
    }

    public MenuResponse create(MenuRequest request) {

        if (menuRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Menu name already exists");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Menu menu = new Menu();
        menu.setName(request.name());
        menu.setDescription(request.description());
        menu.setPrice(request.price());
        menu.setImageUrl(request.imageUrl());
        menu.setIsAvailable(request.isAvailable());
        menu.setCategory(category);

        Menu saved = menuRepository.save(menu);

        return toResponse(saved);
    }

    public MenuResponse findById(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        return toResponse(menu);
    }

    public void delete(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        menuRepository.delete(menu);
    }

    public MenuResponse restore(UUID id) {
        Menu menu = menuRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (Boolean.TRUE.equals(menu.getIsActive())) {
            throw new IllegalArgumentException("Menu already active");
        }

        menu.setIsActive(true);
        Menu saved = menuRepository.save(menu);

        return toResponse(saved);
    }
}
