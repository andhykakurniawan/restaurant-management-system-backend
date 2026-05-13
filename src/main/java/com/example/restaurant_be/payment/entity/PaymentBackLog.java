package com.example.restaurant_be.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_back_log")
@SQLDelete(sql = "UPDATE payment_back_log SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
public class PaymentBackLog extends BaseEntity {
    
    private String midtransOrderId;

    private String transactionStatus;

    private String paymentType;

    private String fraudStatus;

    private String transactionId;

    private LocalDateTime receivedAt;

    private BigDecimal grossAmount;

    private LocalDateTime transactionTime;

    private LocalDateTime settlementTime;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;
}