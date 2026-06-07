package com.example.demo.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Account;

public interface AccountRepository
        extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(
            String accountNumber);
}