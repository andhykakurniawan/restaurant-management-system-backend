package com.example.restaurant_be.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.restaurant_be.category.entity.Category;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM categories", nativeQuery = true)
    List<Category> findAllIncludingInactive();

    @Query(value = "SELECT * FROM categories WHERE id = :id", nativeQuery = true)
    Optional<Category> findByIdIncludingInactive(@Param("id") UUID id);
}
