package com.example.demo.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop10ByAccountAccountIdOrderByTransactionDateDesc(Long accountId);
    List<Transaction> findByAccount_AccountIdOrderByTransactionDateDesc(Long accountId);

    long countByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
    long countByAccount_Branch_BranchIdAndTransactionDateBetween(Long branchId, LocalDateTime from, LocalDateTime to);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t " +
           "where t.transactionType = :type and t.transactionDate between :from and :to")
    double sumAmountByTypeBetween(@Param("type") String type, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t " +
           "where t.transactionType = :type and t.account.branch.branchId = :branchId " +
           "and t.transactionDate between :from and :to")
    double sumAmountByTypeAndBranchBetween(@Param("type") String type, @Param("branchId") Long branchId,
                                            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
