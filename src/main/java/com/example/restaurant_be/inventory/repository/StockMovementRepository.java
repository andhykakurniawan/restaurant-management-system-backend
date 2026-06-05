package com.example.restaurant_be.inventory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restaurant_be.inventory.entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByIngredient_IdOrderByCreatedAtDesc(UUID ingredientId);
}
