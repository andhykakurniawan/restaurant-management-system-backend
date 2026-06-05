package com.example.restaurant_be.order.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
    
public record OrderRequest (
    @NotNull
    UUID sessionId
 ) {}
