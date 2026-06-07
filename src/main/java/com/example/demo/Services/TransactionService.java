package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.TransactionRepository;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Deposit
    public void deposit(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account Not Found"));

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccount(account);
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setBalanceAfterTransaction(account.getBalance());
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    // Withdraw
    public void withdraw(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account Not Found"));

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance");
        }

        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccount(account);
        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(amount);
        transaction.setBalanceAfterTransaction(account.getBalance());
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    // Mini Statement
    public List<Transaction> getTransactions(Long accountId) {

        return transactionRepository.findByAccountAccountId(accountId);
    }
    
    
}