package com.example.store.controller;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderRequest;
import com.example.store.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public CursorPageResponse<OrderDTO> getAllOrders(
            @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "10") int size) {
        return orderService.fetchAll(cursor, size);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.fetchOrder(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @PostMapping("/{orderId}/products/{productId}")
    public OrderDTO addProductToOrder(@PathVariable Long orderId, @PathVariable Long productId) {
        return orderService.addProduct(orderId, productId);
    }
}
