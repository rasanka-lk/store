package com.example.store.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderRequest {
    private String description;

    private Long customerId;

    private List<Long> productIds = new ArrayList<>();
}
