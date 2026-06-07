package com.example.demo.Repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.example.demo.entity.Transaction;

public interface TransactionRepository
extends JpaRepository<Transaction, Long> {

List<Transaction> findByAccountAccountId(Long accountId);
}