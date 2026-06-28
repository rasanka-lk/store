package com.example.store.service;

import com.example.store.dto.CursorPageResponse;
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
import com.example.store.util.CursorPaginationUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public OrderDTO create(OrderRequest request) {
        Order order = new Order();
        order.setDescription(request.description());
        order.setCustomer(resolveCustomer(request));
        order.setProducts(resolveProducts(request.productIds()));
        order.getProducts().forEach(product -> product.getOrders().add(order));
        return orderMapper.orderToOrderDTO(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<OrderDTO> fetchAll(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<Order> items = orderRepository.fetchNextPage(cursor, pageable);

        return CursorPaginationUtil.buildResponse(items, size, Order::getId, orderMapper::ordersToOrderDTOs);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#id")
    public OrderDTO fetchOrder(Long id) {
        return orderMapper.orderToOrderDTO(orderRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID -" + id)));
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public OrderDTO addProduct(Long orderId, Long productId) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found - " + orderId));
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found - " + productId));

        boolean alreadyLinked = order.getProducts().stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .anyMatch(id -> id.equals(productId));

        // Ignoring already added products
        if (!alreadyLinked) {
            order.getProducts().add(product);
            product.getOrders().add(order);
        }

        return orderMapper.orderToOrderDTO(orderRepository.save(order));
    }

    private Customer resolveCustomer(OrderRequest request) {
        return customerRepository
                .findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found - " + request.customerId()));
    }

    private Set<Product> resolveProducts(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.stream().distinct().count()) {
            throw new EntityNotFoundException("One or more products were not found");
        }
        return new HashSet<>(products);
    }
}
