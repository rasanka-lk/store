package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.CustomerRequest;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerDTO create(CustomerRequest customerRequest) {
        Customer customer = customerMapper.customerRequestToCustomer(customerRequest);
        return customerMapper.customerToCustomerDTO(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> fetchAll(int page, int size, String queryString) {
        Customer probe = new Customer();
        probe.setName(queryString);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase("name")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Customer> example = Example.of(probe, matcher);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return customerMapper.customersToCustomerDTOs(
                customerRepository.findAll(example, pageable).getContent());
    }
}
