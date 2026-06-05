package com.example.restaurant_be.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payments_order_id", columnNames = "order_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id 
    @GeneratedValue
    private UUID id;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @JoinColumn(name = "cashier_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User cashier;

    private LocalDateTime paidAt = LocalDateTime.now();
}
