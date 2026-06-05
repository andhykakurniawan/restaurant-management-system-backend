package com.example.restaurant_be.audit.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restaurant_be.audit.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
}
