package com.example.restaurant_be.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.menu.entity.Menu;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM menus", nativeQuery = true)
    List<Menu> findAllIncludingInactive();

    @Query(value = "SELECT * FROM menus WHERE id = :id", nativeQuery = true)
    Optional<Menu> findByIdIncludingInactive(UUID id);
}
