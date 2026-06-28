package com.example.store.controller;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductRequest;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProductReturnsCreatedProduct() throws Exception {
        ProductRequest request = new ProductRequest("Coffee");

        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setDescription("Coffee");
        response.setOrderIds(List.of());

        when(productService.create(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Coffee"));
    }

    @Test
    void getProductByIdReturnsOrderIds() throws Exception {
        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setDescription("Coffee");
        response.setOrderIds(List.of(100L));

        when(productService.fetchProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Coffee"))
                .andExpect(jsonPath("$.orderIds[0]").value(100));
    }

    @Test
    void getAllProductsReturnsCursorPage() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setDescription("Coffee");

        when(productService.fetchProducts(null, 10))
                .thenReturn(new CursorPageResponse<>(List.of(product), 10, null, false));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].description").value("Coffee"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }
}
