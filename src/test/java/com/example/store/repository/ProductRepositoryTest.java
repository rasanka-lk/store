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
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveAndFindByIdPersistsProduct() {
        Product product = new Product();
        product.setDescription("Coffee");

        Product savedProduct = productRepository.saveAndFlush(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(productRepository.findById(savedProduct.getId()))
                .isPresent()
                .get()
                .extracting(Product::getDescription)
                .isEqualTo("Coffee");
    }

    @Test
    void findByIdLoadsOrdersForProduct() {
        Customer customer = saveCustomer("John Doe");
        Product product = saveProduct("Coffee");

        Order order = new Order();
        order.setDescription("Morning order");
        order.setCustomer(customer);
        order.getProducts().add(product);
        product.getOrders().add(order);
        orderRepository.saveAndFlush(order);

        assertThat(productRepository.findById(product.getId()))
                .isPresent()
                .get()
                .satisfies(foundProduct -> assertThat(foundProduct.getOrders())
                        .extracting(Order::getDescription)
                        .containsExactly("Morning order"));
    }

    @Test
    void fetchNextPageReturnsProductsAfterCursorInIdOrder() {
        Product first = saveProduct("Coffee");
        Product second = saveProduct("Tea");
        Product third = saveProduct("Muffin");

        List<Product> results = productRepository.fetchNextPage(first.getId(), PageRequest.of(0, 2));

        assertThat(results).extracting(Product::getId).containsExactly(second.getId(), third.getId());
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
}
