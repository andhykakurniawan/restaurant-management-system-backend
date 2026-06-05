package com.example.restaurant_be.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.table.entity.TableRestaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@SQLDelete(sql = "UPDATE bookings SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
public class Booking extends BaseEntity {

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private Integer numberPerson;

    @JoinColumn(name = "table_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TableRestaurant table;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private LocalTime bookingTime;

    @Column(nullable = false)
    private Integer durationMinutes = 90;

    @Column(nullable = false, unique = true)
    private String bookingCode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dpAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String snapToken;

    @Column(nullable = false, unique = true)
    private String midtransOrderId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(nullable = false)
    private boolean hasPreorder = false;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime noShowAt;
}
