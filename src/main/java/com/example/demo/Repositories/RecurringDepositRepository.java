package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.RecurringDeposit;

public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, Long> {

    List<RecurringDeposit> findByAccount_AccountId(Long accountId);
}
