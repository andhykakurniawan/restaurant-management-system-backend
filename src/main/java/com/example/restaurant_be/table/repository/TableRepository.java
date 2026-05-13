package com.example.restaurant_be.table.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.table.entity.AllocationType;
import com.example.restaurant_be.table.entity.TableRestaurant;

public interface TableRepository extends JpaRepository<TableRestaurant, UUID> {
    boolean existsByTableNumber(String tableNumber);

    @Query(value = "SELECT * FROM table_restaurants", nativeQuery = true)
    List<TableRestaurant> findAllIncludingInactive();

    @Query(value = "SELECT * FROM table_restaurants WHERE id = :id", nativeQuery = true)
    Optional<TableRestaurant> findByIdIncludingInactive(UUID id);

    Optional<TableRestaurant> findByIdAndAllocationType(UUID id, AllocationType allocationType);
}
