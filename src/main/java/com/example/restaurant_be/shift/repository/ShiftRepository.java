package com.example.restaurant_be.shift.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.restaurant_be.shift.entity.Shift;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    @Query(value = "SELECT * FROM shifts", nativeQuery = true)
    List<Shift> findAllIncludingInactive();

    @Query(value = "SELECT * FROM shifts WHERE id = :id", nativeQuery = true)
    Optional<Shift> findByIdIncludingInactive(UUID id);
}