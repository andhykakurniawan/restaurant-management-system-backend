package com.example.restaurant_be.inventory.entity;

import java.math.BigDecimal;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.ingredient.entity.Ingredients;
import com.example.restaurant_be.order.entity.Order;

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
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
public class StockMovement extends BaseEntity {

    @JoinColumn(name = "ingredient_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Ingredients ingredient;

    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal stockBefore;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal stockAfter;

    @Column(length = 255)
    private String note;
}
