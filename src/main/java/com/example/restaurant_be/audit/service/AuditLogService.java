package com.example.restaurant_be.audit.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.audit.dto.AuditLogResponse;
import com.example.restaurant_be.audit.entity.AuditLog;
import com.example.restaurant_be.audit.repository.AuditLogRepository;
import com.example.restaurant_be.user.entity.User;
import com.example.restaurant_be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public List<AuditLogResponse> findAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void log(String action, String entityType, UUID entityId, String detail) {
        User actor = getAuthenticatedUserOrNull();

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setActor(actor);
        auditLog.setActorName(actor != null ? actor.getUsername() : "SYSTEM");
        auditLog.setActorRole(actor != null ? actor.getRole() : null);
        auditLog.setDetail(detail);

        auditLogRepository.save(auditLog);
    }

    private User getAuthenticatedUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName())
                .orElse(null);
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getActor() != null ? auditLog.getActor().getId() : null,
                auditLog.getActorName(),
                auditLog.getActorRole(),
                auditLog.getDetail(),
                auditLog.getCreatedAt());
    }
}
