package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "Customer name is required")
                @Size(max = 255, message = "Customer name must not exceed 255 characters")
                String name) {}
