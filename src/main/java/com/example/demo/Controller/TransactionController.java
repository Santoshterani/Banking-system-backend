package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.TransactionService;
import com.example.demo.entity.Transaction;

@RestController
@RequestMapping("/admin/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Deposit
    @PostMapping("/deposit/{accountId}/{amount}")
    public String deposit(
            @PathVariable Long accountId,
            @PathVariable Double amount) {

        transactionService.deposit(accountId, amount);
        return "Amount Deposited Successfully";
    }

    // Withdraw
    @PostMapping("/withdraw/{accountId}/{amount}")
    public String withdraw(
            @PathVariable Long accountId,
            @PathVariable Double amount) {

        transactionService.withdraw(accountId, amount);
        return "Amount Withdrawn Successfully";
    }

    // Mini Statement
    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactions(
            @PathVariable Long accountId) {

        return transactionService.getTransactions(accountId);
    }
}