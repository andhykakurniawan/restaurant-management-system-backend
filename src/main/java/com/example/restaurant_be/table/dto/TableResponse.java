package com.example.restaurant_be.table.dto;

import java.util.UUID;

import com.example.restaurant_be.table.entity.AllocationType;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.entity.TableArea;

public record TableResponse(
    UUID id,
    String tableNumber,
    Integer capacity,
    AllocationType allocationType,
    TableStatus status,
    TableArea area,
    boolean is_active
) {
    
}
