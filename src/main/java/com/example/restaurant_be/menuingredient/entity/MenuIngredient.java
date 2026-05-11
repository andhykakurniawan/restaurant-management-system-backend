package com.example.restaurant_be.menuingredient.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.example.restaurant_be.common.base.BaseEntity;
import com.example.restaurant_be.ingredient.entity.Ingredients;
import com.example.restaurant_be.menu.entity.Menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menu_ingredients")
@SQLDelete(sql = "UPDATE menu_ingredients SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@Setter
public class MenuIngredient extends BaseEntity {
    @JoinColumn(name = "menu_id", nullable = false)
    @ManyToOne
    private Menu menu;

    @JoinColumn(name = "ingredient_id", nullable = false)
    @ManyToOne
    private Ingredients ingredient;

    @Column(nullable = false)
    private Integer quantity;
    
}
