package com.example.restaurant_be.ordersession.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.table.entity.TableRestaurant;

public interface OrderSessionRepository extends JpaRepository<OrderSession, UUID> {
    @Query(value = "select * from order_sessions where id = :id", nativeQuery = true)
    Optional<OrderSession> findByIdIncludingInactive(UUID id);

    @Query(value = "select * from order_sessions where user_id = :userId", nativeQuery = true)
    List<OrderSession> findByUserIdIncludingInactive(UUID userId);

    @Query(value = "select * from order_sessions where table_id = :tableId", nativeQuery = true)
    List<OrderSession> findByTableIdIncludingInactive(UUID tableId);

    @Query(value = "select * from order_sessions", nativeQuery = true)
    List<OrderSession> findAllIncludingInactive();

    boolean existsByTableAndIsActiveTrue(
            TableRestaurant table);

    List<OrderSession> findByExpiredAtBeforeAndStatusIn(
            LocalDateTime now,
            List<SessionStatus> statuses);

}
