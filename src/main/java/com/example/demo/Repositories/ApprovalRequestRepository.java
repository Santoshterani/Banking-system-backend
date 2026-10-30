package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ApprovalRequest;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    List<ApprovalRequest> findByStatus(String status);
    List<ApprovalRequest> findByBranch_BranchIdAndStatus(Long branchId, String status);
}
