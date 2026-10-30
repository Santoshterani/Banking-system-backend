package com.example.demo.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByBranch_BranchId(Long branchId);
    Page<Account> findByBranch_BranchId(Long branchId, Pageable pageable);
    long countByBranch_BranchId(Long branchId);

    long countByCustomer_CustomerId(Long customerId);

    @Query("select coalesce(sum(a.balance), 0) from Account a")
    double sumAllBalances();

    @Query("select coalesce(sum(a.balance), 0) from Account a where a.branch.branchId = :branchId")
    double sumBalancesByBranch(Long branchId);
}
