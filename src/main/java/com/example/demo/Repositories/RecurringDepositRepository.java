package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RecurringDeposit;

public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, Long> {

    List<RecurringDeposit> findByCustomer_CustomerId(Long customerId);
}
