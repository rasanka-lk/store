package com.example.store.controller;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderRequest;
import com.example.store.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderAcceptsProductIds() throws Exception {
        OrderRequest request = new OrderRequest("Test Order", 1L, Set.of(10L, 20L));

        OrderDTO response = new OrderDTO();
        response.setId(100L);
        response.setDescription("Test Order");

        when(orderService.create(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    @Test
    void getOrdersReturnsCursorPage() throws Exception {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setDescription("Test Order");

        when(orderService.fetchAll(null, 10)).thenReturn(new CursorPageResponse<>(List.of(order), 10, null, false));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].description").value("Test Order"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    void addProductToOrderReturnsUpdatedOrder() throws Exception {
        OrderDTO response = new OrderDTO();
        response.setId(1L);
        response.setDescription("Updated Order");

        when(orderService.addProduct(eq(1L), eq(10L))).thenReturn(response);

        mockMvc.perform(post("/order/1/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Updated Order"));
    }
}
