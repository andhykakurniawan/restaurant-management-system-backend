package com.example.restaurant_be.inventory.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.common.exception.BadRequestException;
import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.ingredient.entity.Ingredients;
import com.example.restaurant_be.ingredient.repository.IngredientRepository;
import com.example.restaurant_be.inventory.dto.LowStockIngredientResponse;
import com.example.restaurant_be.inventory.dto.StockAdjustmentRequest;
import com.example.restaurant_be.inventory.dto.StockMovementResponse;
import com.example.restaurant_be.inventory.entity.StockMovement;
import com.example.restaurant_be.inventory.entity.StockMovementType;
import com.example.restaurant_be.inventory.repository.StockMovementRepository;
import com.example.restaurant_be.menuingredient.entity.MenuIngredient;
import com.example.restaurant_be.menuingredient.repository.MenuIngredientRepository;
import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.OrderItem;
import com.example.restaurant_be.order.repository.OrderItemRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final MenuIngredientRepository menuIngredientRepository;
    private final OrderItemRepository orderItemRepository;
    private final StockMovementRepository stockMovementRepository;

    public List<StockMovementResponse> findAllMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StockMovementResponse> findMovementsByIngredient(UUID ingredientId) {
        return stockMovementRepository.findByIngredient_IdOrderByCreatedAtDesc(ingredientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LowStockIngredientResponse> findLowStockIngredients() {
        return ingredientRepository.findLowStock()
                .stream()
                .map(ingredient -> new LowStockIngredientResponse(
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getUnit(),
                        ingredient.getCurrentStock(),
                        ingredient.getMinimumStock()))
                .toList();
    }

    @Transactional
    public StockMovementResponse adjustStock(UUID ingredientId, StockAdjustmentRequest request) {
        Ingredients ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        BigDecimal stockBefore = ingredient.getCurrentStock();
        BigDecimal stockAfter = calculateAdjustedStock(stockBefore, request);

        if (stockAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("Ingredient stock cannot be negative");
        }

        ingredient.setCurrentStock(stockAfter);
        ingredientRepository.save(ingredient);

        StockMovement movement = saveMovement(
                ingredient,
                null,
                request.type(),
                request.quantity(),
                stockBefore,
                stockAfter,
                request.note());

        return toResponse(movement);
    }

    @Transactional
    public void deductStockForOrder(Order order) {
        Map<UUID, BigDecimal> requiredStock = calculateRequiredStock(order);

        for (Map.Entry<UUID, BigDecimal> entry : requiredStock.entrySet()) {
            Ingredients ingredient = ingredientRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("Ingredient not found"));

            BigDecimal quantity = entry.getValue();
            BigDecimal stockBefore = ingredient.getCurrentStock();

            if (stockBefore.compareTo(quantity) < 0) {
                throw new ConflictException(
                        "Insufficient stock for ingredient: " + ingredient.getName());
            }

            BigDecimal stockAfter = stockBefore.subtract(quantity);
            ingredient.setCurrentStock(stockAfter);
            ingredientRepository.save(ingredient);

            saveMovement(
                    ingredient,
                    order,
                    StockMovementType.OUT,
                    quantity,
                    stockBefore,
                    stockAfter,
                    "Order confirmed: " + order.getOrderCode());
        }
    }

    @Transactional
    public void restoreStockForOrder(Order order) {
        Map<UUID, BigDecimal> requiredStock = calculateRequiredStock(order);

        for (Map.Entry<UUID, BigDecimal> entry : requiredStock.entrySet()) {
            Ingredients ingredient = ingredientRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("Ingredient not found"));

            BigDecimal quantity = entry.getValue();
            BigDecimal stockBefore = ingredient.getCurrentStock();
            BigDecimal stockAfter = stockBefore.add(quantity);

            ingredient.setCurrentStock(stockAfter);
            ingredientRepository.save(ingredient);

            saveMovement(
                    ingredient,
                    order,
                    StockMovementType.RESTORE,
                    quantity,
                    stockBefore,
                    stockAfter,
                    "Order cancelled: " + order.getOrderCode());
        }
    }

    private Map<UUID, BigDecimal> calculateRequiredStock(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(order.getId());

        if (orderItems.isEmpty()) {
            throw new ConflictException("Order must have at least one item");
        }

        Map<UUID, BigDecimal> requiredStock = new LinkedHashMap<>();

        for (OrderItem item : orderItems) {
            List<MenuIngredient> recipes = menuIngredientRepository.findByMenu_Id(item.getMenu().getId());

            if (recipes.isEmpty()) {
                throw new ConflictException("Menu has no ingredient recipe: " + item.getMenu().getName());
            }

            for (MenuIngredient recipe : recipes) {
                BigDecimal quantity = BigDecimal.valueOf(recipe.getQuantity())
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                requiredStock.merge(
                        recipe.getIngredient().getId(),
                        quantity,
                        BigDecimal::add);
            }
        }

        return requiredStock;
    }

    private BigDecimal calculateAdjustedStock(BigDecimal stockBefore, StockAdjustmentRequest request) {
        return switch (request.type()) {
            case IN, RESTORE -> stockBefore.add(request.quantity());
            case OUT -> stockBefore.subtract(request.quantity());
            case ADJUSTMENT -> request.quantity();
        };
    }

    private StockMovement saveMovement(
            Ingredients ingredient,
            Order order,
            StockMovementType type,
            BigDecimal quantity,
            BigDecimal stockBefore,
            BigDecimal stockAfter,
            String note) {

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Stock movement quantity must be positive");
        }

        StockMovement movement = new StockMovement();
        movement.setIngredient(ingredient);
        movement.setOrder(order);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setStockBefore(stockBefore);
        movement.setStockAfter(stockAfter);
        movement.setNote(note);

        return stockMovementRepository.save(movement);
    }

    private StockMovementResponse toResponse(StockMovement movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getIngredient().getId(),
                movement.getIngredient().getName(),
                movement.getOrder() != null ? movement.getOrder().getId() : null,
                movement.getType(),
                movement.getQuantity(),
                movement.getStockBefore(),
                movement.getStockAfter(),
                movement.getNote());
    }
}
