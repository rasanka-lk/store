package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "Description is required")
                @Size(max = 255, message = "Description cannot be more than 255 characters")
                String description) {}
