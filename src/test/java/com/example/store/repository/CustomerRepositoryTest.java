package com.example.store.repository;

import com.example.store.entity.Customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void saveAndFindByIdPersistsCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");

        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(customerRepository.findById(savedCustomer.getId()))
                .isPresent()
                .get()
                .extracting(Customer::getName)
                .isEqualTo("John Doe");
    }
}
