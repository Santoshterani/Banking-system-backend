package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerCustomerId(Long customerId);

    List<Loan> findByCustomer_Branch_BranchId(Long branchId);
    Page<Loan> findByCustomer_Branch_BranchId(Long branchId, Pageable pageable);

    long countByStatus(String status);
    long countByCustomer_Branch_BranchIdAndStatus(Long branchId, String status);
}
