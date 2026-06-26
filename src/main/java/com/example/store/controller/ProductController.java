package com.example.store.controller;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductRequest;
import com.example.store.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public CursorPageResponse<ProductDTO> getAllProducts(
            @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "10") int size) {
        return productService.fetchProducts(cursor, size);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.fetchProductById(id);
    }

    @Operation(summary = "Create a new product")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
