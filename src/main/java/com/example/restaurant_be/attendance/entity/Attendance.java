package com.example.restaurant_be.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.shift.entity.Shift;
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
@Table(name = "attendances")
@SQLDelete(sql = "UPDATE attendances SET is_active = false WHERE id = ? ")
@SQLRestriction("is_active = true")
@Getter
@Setter
@NoArgsConstructor
public class Attendance extends BaseEntity {
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "shifts_id", nullable = false)
    @ManyToOne
    private Shift shift;

    private LocalDate attendanceDate;

    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String notes;
}