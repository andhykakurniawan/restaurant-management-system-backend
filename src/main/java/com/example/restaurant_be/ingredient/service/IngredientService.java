package com.example.restaurant_be.ingredient.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.ingredient.dto.IngredientRequest;
import com.example.restaurant_be.ingredient.dto.IngredientResponse;
import com.example.restaurant_be.ingredient.entity.Ingredients;
import com.example.restaurant_be.ingredient.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<IngredientResponse> findAll() {
        return ingredientRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private IngredientResponse toResponse(Ingredients ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getUnit(),
                ingredient.getCurrentStock(),
                ingredient.getMinimumStock(),
                ingredient.getCostPerUnit(),
                ingredient.getIsActive());
    }

    public IngredientResponse create(IngredientRequest request) {

        if (ingredientRepository.existsByName(request.name())) {
            throw new ConflictException("Ingredient name already exists");
        }

        Ingredients ingredient = new Ingredients();
        ingredient.setName(request.name());
        ingredient.setUnit(request.unit());
        ingredient.setCurrentStock(request.currentstock());
        ingredient.setMinimumStock(request.minimumstock());
        ingredient.setCostPerUnit(request.costperunit());

        Ingredients saved = ingredientRepository.save(ingredient);

        return toResponse(saved);
    }

    public IngredientResponse findById(UUID id) {
        Ingredients ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        return toResponse(ingredient);
    }

    public IngredientResponse update(UUID id, IngredientRequest request) {
        Ingredients ingredient = ingredientRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        if (!ingredient.getName().equals(request.name()) &&
                ingredientRepository.existsByName(request.name())) {
            throw new ConflictException("Ingredient name already exists");
        }

        ingredient.setName(request.name());
        ingredient.setUnit(request.unit());
        ingredient.setCurrentStock(request.currentstock());
        ingredient.setMinimumStock(request.minimumstock());
        ingredient.setCostPerUnit(request.costperunit());

        Ingredients saved = ingredientRepository.save(ingredient);

        return toResponse(saved);
    }

    public void deleteById(UUID id) {
        Ingredients ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        ingredientRepository.delete(ingredient);
    }

    public IngredientResponse restore(UUID id) {

        Ingredients ingredient = ingredientRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        if (Boolean.TRUE.equals(ingredient.getIsActive())) {
            throw new ConflictException("Ingredient already active");
        }

        ingredient.setIsActive(true);
        Ingredients restored = ingredientRepository.save(ingredient);

        return toResponse(restored);
    }
}
