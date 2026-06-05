package com.example.restaurant_be.audit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.restaurant_be.user.entity.Role;

public record AuditLogResponse(
        UUID id,
        String action,
        String entityType,
        UUID entityId,
        UUID actorId,
        String actorName,
        Role actorRole,
        String detail,
        LocalDateTime createdAt) {
}
