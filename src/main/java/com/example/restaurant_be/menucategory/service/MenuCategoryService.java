package com.example.restaurant_be.menucategory.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.common.exception.NotFoundException;

import com.example.restaurant_be.category.entity.Category;
import com.example.restaurant_be.category.repository.CategoryRepository;
import com.example.restaurant_be.menu.entity.Menu;
import com.example.restaurant_be.menu.repository.MenuRepository;
import com.example.restaurant_be.menucategory.dto.MenuCategoryRequest;
import com.example.restaurant_be.menucategory.dto.MenuCategoryResponse;
import com.example.restaurant_be.menucategory.entity.MenuCategory;
import com.example.restaurant_be.menucategory.repository.MenuCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuCategoryService {
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public List<MenuCategoryResponse> findAll() {
        return menuCategoryRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MenuCategoryResponse toResponse(MenuCategory menuCategory) {
        return new MenuCategoryResponse(
                menuCategory.getId(),
                menuCategory.getMenu().getId(),
                menuCategory.getMenu().getName(),
                menuCategory.getCategory().getId(),
                menuCategory.getCategory().getName(),
                menuCategory.getPrice(),
                menuCategory.getIsActive());
    }

    public MenuCategoryResponse create(MenuCategoryRequest request) {

        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new NotFoundException("Menu not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        MenuCategory menuCategory = new MenuCategory();
        menuCategory.setMenu(menu);
        menuCategory.setCategory(category);
        menuCategory.setPrice(request.price());

        MenuCategory saved = menuCategoryRepository.save(menuCategory);

        return toResponse(saved);
    }

    public MenuCategoryResponse findById(UUID id) {
        MenuCategory menuCategory = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MenuCategory not found"));

        return toResponse(menuCategory);
    }

    public List<MenuCategoryResponse> findByMenuId(UUID menuId) {
        return menuCategoryRepository.findByMenuIdIncludingInactive(menuId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MenuCategoryResponse> findByCategoryId(UUID categoryId) {
        return menuCategoryRepository.findByCategoryIdIncludingInactive(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MenuCategoryResponse updatePartial(UUID id, Map<String, Object> updates) {

        MenuCategory entity = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data not found"));

        if (updates.containsKey("price")) {
            BigDecimal price = new BigDecimal(updates.get("price").toString());
            entity.setPrice(price);
        }

        MenuCategory saved = menuCategoryRepository.save(entity);
        return toResponse(saved);
    }

    public MenuCategoryResponse update(UUID id, MenuCategoryRequest request) {
        MenuCategory menuCategory = menuCategoryRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new NotFoundException("MenuCategory not found"));

        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new NotFoundException("Menu not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        menuCategory.setMenu(menu);
        menuCategory.setCategory(category);
        menuCategory.setPrice(request.price());

        MenuCategory saved = menuCategoryRepository.save(menuCategory);

        return toResponse(saved);
    }

    public void deleteById(UUID id) {
        if (!menuCategoryRepository.existsById(id)) {
            throw new NotFoundException("MenuCategory not found");
        }
        menuCategoryRepository.deleteById(id);
    }

    public MenuCategoryResponse restore(UUID id) {
        MenuCategory menuCategory = menuCategoryRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new NotFoundException("MenuCategory not found"));

        menuCategory.setIsActive(true);
        MenuCategory restored = menuCategoryRepository.save(menuCategory);

        return toResponse(restored);
    }
}

