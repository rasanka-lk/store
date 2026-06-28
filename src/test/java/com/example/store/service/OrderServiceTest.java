package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.OrderRequest;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.exception.EntityNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderCanIncludeExistingProductIds() {
        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setDescription("Coffee");

        OrderRequest request = new OrderRequest("Morning order", 1L, Set.of(10L));

        OrderDTO mappedResponse = new OrderDTO();
        mappedResponse.setId(100L);
        mappedResponse.setDescription("Morning order");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.orderToOrderDTO(any(Order.class))).thenReturn(mappedResponse);

        OrderDTO result = orderService.create(request);

        assertThat(result.getDescription()).isEqualTo("Morning order");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getDescription()).isEqualTo("Morning order");
        assertThat(savedOrder.getCustomer()).isSameAs(customer);
        assertThat(savedOrder.getProducts()).containsExactly(product);
        assertThat(product.getOrders()).contains(savedOrder);
    }

    @Test
    void createOrderThrowsWhenAProductIdDoesNotExist() {
        Customer customer = new Customer();
        customer.setId(1L);

        OrderRequest request = new OrderRequest("Morning order", 1L, Set.of(10L, 20L));

        Product product = new Product();
        product.setId(10L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("One or more products were not found");
    }

    @Test
    void addProductLinksExistingProductToExistingOrder() {
        Order order = new Order();
        order.setId(1L);

        Product product = new Product();
        product.setId(10L);

        OrderDTO mappedResponse = new OrderDTO();
        mappedResponse.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderToOrderDTO(order)).thenReturn(mappedResponse);

        OrderDTO result = orderService.addProduct(1L, 10L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(order.getProducts()).containsExactly(product);
        assertThat(product.getOrders()).containsExactly(order);
    }
}
