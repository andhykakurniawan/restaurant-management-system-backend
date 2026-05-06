package com.example.restaurant_be.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<User> findByIdIncludingInactive(UUID id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> findAllIncludingInactive();
}
