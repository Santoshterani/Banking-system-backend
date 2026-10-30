package com.example.demo.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByAadhaarNumber(String aadhaarNumber);
    Optional<Customer> findByPanNumber(String panNumber);

    List<Customer> findByBranch_BranchId(Long branchId);
    Page<Customer> findByBranch_BranchId(Long branchId, Pageable pageable);
    long countByBranch_BranchId(Long branchId);
}
