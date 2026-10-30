package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RecurringDeposit;

public interface RDRepository extends JpaRepository<RecurringDeposit, Long> {

    List<RecurringDeposit> findByCustomerCustomerId(Long customerId);
    List<RecurringDeposit> findByCustomer_Branch_BranchId(Long branchId);
    List<RecurringDeposit> findByStatus(String status);
}
