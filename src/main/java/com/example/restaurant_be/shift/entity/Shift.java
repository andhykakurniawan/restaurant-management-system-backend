package com.example.restaurant_be.shift.entity;

import java.time.LocalTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.example.restaurant_be.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shifts")
@SQLDelete(sql = "UPDATE shifts SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
public class Shift extends BaseEntity {
    @Column(nullable = false)
    private String shiftName;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer graceMinutes;
}
