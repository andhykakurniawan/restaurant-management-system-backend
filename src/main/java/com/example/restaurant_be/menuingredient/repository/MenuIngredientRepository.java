package com.example.restaurant_be.menuingredient.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.menuingredient.entity.MenuIngredient;

public interface MenuIngredientRepository extends JpaRepository<MenuIngredient, UUID> {
    @Query(value = "select * from menu_ingredients where id = :id", nativeQuery = true)
    Optional<MenuIngredient> findByIdIncludingInactive(UUID id);

    @Query(value = "select * from menu_ingredients", nativeQuery = true)
    List<MenuIngredient> findAllIncludingInactive();
}
