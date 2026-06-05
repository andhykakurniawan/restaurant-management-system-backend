package com.example.restaurant_be.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank
    String name,

    @NotBlank
    String description
) {}
