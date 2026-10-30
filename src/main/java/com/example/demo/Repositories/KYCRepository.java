package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KYC;

public interface KYCRepository extends JpaRepository<KYC, Long> {

    KYC findByCustomerCustomerId(Long customerId);
    List<KYC> findByCustomer_Branch_BranchId(Long branchId);

    long countByStatus(String status);
    long countByCustomer_Branch_BranchIdAndStatus(Long branchId, String status);
}
