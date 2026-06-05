package com.example.restaurant_be.order.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {
    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @JoinColumn(name = "created_by", nullable = false)
    @ManyToOne
    private User createdBy;

    @JoinColumn(name = "order_session_id", nullable = false)
    @ManyToOne
    private OrderSession orderSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
