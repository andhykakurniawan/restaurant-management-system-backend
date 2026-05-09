package com.example.restaurant_be.table.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "table_restaurants")
@SQLDelete(sql = "UPDATE table_restaurants SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
@NoArgsConstructor
public class TableRestaurant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationType allocationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableArea area;

}
