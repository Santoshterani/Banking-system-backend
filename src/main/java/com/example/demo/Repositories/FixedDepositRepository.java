package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.FixedDeposit;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {

    List<FixedDeposit> findByAccount_AccountId(Long accountId);
}
