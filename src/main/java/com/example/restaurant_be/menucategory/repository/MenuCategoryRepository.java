package com.example.restaurant_be.menucategory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.restaurant_be.menucategory.entity.MenuCategory;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    @Query(value = "select * from menu_categories where id = :id", nativeQuery = true)
    Optional<MenuCategory> findByIdIncludingInactive(UUID id);

    @Query(value = "select * from menu_categories where menu_id = :menuId", nativeQuery = true)
    List<MenuCategory> findByMenuIdIncludingInactive(UUID menuId);

    @Query(value = "select * from menu_categories where category_id = :categoryId", nativeQuery = true)
    List<MenuCategory> findByCategoryIdIncludingInactive(UUID categoryId);

    @Query(value = "select * from menu_categories", nativeQuery = true)
    List<MenuCategory> findAllIncludingInactive();
}