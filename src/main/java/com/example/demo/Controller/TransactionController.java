package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> deposit(
            @PathVariable Long accountId,
            @PathVariable Double amount) {

        try {
            transactionService.deposit(accountId, amount);
            return ResponseEntity.ok("Amount Deposited Successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Withdraw
    @PostMapping("/withdraw/{accountId}/{amount}")
    public ResponseEntity<?> withdraw(
            @PathVariable Long accountId,
            @PathVariable Double amount) {

        try {
            transactionService.withdraw(accountId, amount);
            return ResponseEntity.ok("Amount Withdrawn Successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Transfer
    @PostMapping("/transfer/{fromAccountId}/{toAccountId}/{amount}")
    public ResponseEntity<?> transfer(
            @PathVariable Long fromAccountId,
            @PathVariable Long toAccountId,
            @PathVariable Double amount) {

        try {

            transactionService.transfer(fromAccountId, toAccountId, amount);

            return ResponseEntity.ok("Money Transferred Successfully");

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        }
    }

    // Mini Statement
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService.getTransactions(accountId));
    }

}