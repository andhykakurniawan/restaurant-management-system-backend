package com.example.restaurant_be.audit.entity;

import java.util.UUID;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.user.entity.Role;
import com.example.restaurant_be.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    private UUID entityId;

    @JoinColumn(name = "actor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User actor;

    private String actorName;

    @Enumerated(EnumType.STRING)
    private Role actorRole;

    @Column(length = 1000)
    private String detail;
}
