package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record OrderRequest(
        @NotBlank(message = "Order description is required")
                @Size(max = 255, message = "Order description must not exceed 255 characters")
                String description,
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotEmpty(message = "Product ID required") Set<Long> productIds) {}
