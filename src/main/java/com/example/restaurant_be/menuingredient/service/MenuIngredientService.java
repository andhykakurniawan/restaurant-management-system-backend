package com.example.restaurant_be.menuingredient.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.menu.entity.Menu;
import com.example.restaurant_be.menu.repository.MenuRepository;
import com.example.restaurant_be.ingredient.entity.Ingredients;
import com.example.restaurant_be.ingredient.repository.IngredientRepository;
import com.example.restaurant_be.menuingredient.dto.MenuIngredientRequest;
import com.example.restaurant_be.menuingredient.dto.MenuIngredientResponse;
import com.example.restaurant_be.menuingredient.entity.MenuIngredient;
import com.example.restaurant_be.menuingredient.repository.MenuIngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuIngredientService {
    private final MenuIngredientRepository menuIngredientRepository;
    private final MenuRepository menuRepository;
    private final IngredientRepository ingredientRepository;

    public List<MenuIngredientResponse> findAll() {
        return menuIngredientRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MenuIngredientResponse toResponse(MenuIngredient menuIngredient) {
        return new MenuIngredientResponse(
                menuIngredient.getId(),
                menuIngredient.getMenu().getId(),
                menuIngredient.getMenu().getName(),
                menuIngredient.getIngredient().getId(),
                menuIngredient.getIngredient().getName(),
                menuIngredient.getQuantity(),
                menuIngredient.getIsActive());
    }

    public MenuIngredientResponse create(MenuIngredientRequest request) {

        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        Ingredients ingredient = ingredientRepository.findById(request.ingredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        MenuIngredient menuIngredient = new MenuIngredient();
        menuIngredient.setMenu(menu);
        menuIngredient.setIngredient(ingredient);
        menuIngredient.setQuantity(request.quantity());

        MenuIngredient saved = menuIngredientRepository.save(menuIngredient);

        return toResponse(saved);
    }

    public MenuIngredientResponse findById(UUID id) {
        MenuIngredient menuIngredient = menuIngredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuIngredient not found"));

        return toResponse(menuIngredient);
    }

    public List<MenuIngredientResponse> findByMenuId(UUID menuId) {
        return menuIngredientRepository.findByMenuIdIncludingInactive(menuId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteById(UUID id) {
        if (!menuIngredientRepository.existsById(id)) {
            throw new IllegalArgumentException("MenuIngredient not found");
        }
        menuIngredientRepository.deleteById(id);
    }

    public MenuIngredientResponse restore(UUID id) {
        MenuIngredient menuIngredient = menuIngredientRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuIngredient not found"));

        menuIngredient.setIsActive(true);
        MenuIngredient restored = menuIngredientRepository.save(menuIngredient);

        return toResponse(restored);
    }
}
