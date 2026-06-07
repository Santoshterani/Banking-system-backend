package com.example.demo.Repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByAadhaarNumber(String aadhaarNumber);

    Optional<Customer> findByPanNumber(String panNumber);
}