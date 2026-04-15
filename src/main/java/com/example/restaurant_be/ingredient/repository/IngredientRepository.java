package com.example.restaurant_be.ingredient.repository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.restaurant_be.ingredient.entity.Ingredients;

public interface IngredientRepository extends JpaRepository<Ingredients, UUID> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM ingredients", nativeQuery = true)
    List<Ingredients> findAllIncludingInactive();
    
    @Query (value = "SELECT * FROM ingredients WHERE id = :id", nativeQuery = true)
    Optional<Ingredients> findByIdIncludingInactive(@Param("id") UUID id);
}
