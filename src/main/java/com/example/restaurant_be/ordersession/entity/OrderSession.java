package com.example.restaurant_be.ordersession.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_sessions")
@SQLDelete(sql = "UPDATE order_sessions SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
public class OrderSession extends BaseEntity {
    @JoinColumn(name = "table_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TableRestaurant table;

    @JoinColumn(name = "created_by", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false, unique = true)
    private String sessionToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime expiredAt;

    private LocalDateTime closedAt;
}
