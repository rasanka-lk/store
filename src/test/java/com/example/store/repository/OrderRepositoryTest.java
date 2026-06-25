package com.example.store.repository;

import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveOrderPersistsCustomerRelationship() {
        Customer customer = saveCustomer("John Doe");

        Order order = new Order();
        order.setDescription("Morning order");
        order.setCustomer(customer);

        Order savedOrder = orderRepository.saveAndFlush(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(orderRepository.findById(savedOrder.getId()))
                .isPresent()
                .get()
                .satisfies(foundOrder -> {
                    assertThat(foundOrder.getDescription()).isEqualTo("Morning order");
                    assertThat(foundOrder.getCustomer().getId()).isEqualTo(customer.getId());
                });
    }

    @Test
    void saveOrderPersistsProductsThroughJoinTable() {
        Customer customer = saveCustomer("Jane Doe");
        Product coffee = saveProduct("Coffee");
        Product muffin = saveProduct("Muffin");

        Order order = new Order();
        order.setDescription("Breakfast order");
        order.setCustomer(customer);
        order.getProducts().addAll(List.of(coffee, muffin));

        Order savedOrder = orderRepository.saveAndFlush(order);

        assertThat(orderRepository.findById(savedOrder.getId()))
                .isPresent()
                .get()
                .satisfies(foundOrder -> assertThat(foundOrder.getProducts())
                        .extracting(Product::getDescription)
                        .containsExactlyInAnyOrder("Coffee", "Muffin"));
    }

    @Test
    void fetchNextPageReturnsOrdersAfterCursorInIdOrder() {
        Customer customer = saveCustomer("Cursor Customer");
        Order first = saveOrder("First order", customer);
        Order second = saveOrder("Second order", customer);
        Order third = saveOrder("Third order", customer);

        List<Order> results = orderRepository.fetchNextPage(first.getId(), PageRequest.of(0, 2));

        assertThat(results).extracting(Order::getId).containsExactly(second.getId(), third.getId());
    }

    private Customer saveCustomer(String name) {
        Customer customer = new Customer();
        customer.setName(name);
        return customerRepository.saveAndFlush(customer);
    }

    private Product saveProduct(String description) {
        Product product = new Product();
        product.setDescription(description);
        return productRepository.saveAndFlush(product);
    }

    private Order saveOrder(String description, Customer customer) {
        Order order = new Order();
        order.setDescription(description);
        order.setCustomer(customer);
        return orderRepository.saveAndFlush(order);
    }
}
