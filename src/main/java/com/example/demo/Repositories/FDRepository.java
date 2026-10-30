package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.FixedDeposit;

public interface FDRepository extends JpaRepository<FixedDeposit, Long> {

    List<FixedDeposit> findByCustomerCustomerId(Long customerId);
    List<FixedDeposit> findByCustomer_Branch_BranchId(Long branchId);
    List<FixedDeposit> findByStatus(String status);
}
