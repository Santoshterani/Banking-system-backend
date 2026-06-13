package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ---------------- DEPOSIT ----------------

    @Transactional
    public void deposit(Long accountId, Double amount) {

        if(amount <= 0)
            throw new RuntimeException("Invalid Amount");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                new RuntimeException("Account Not Found"));

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);

        Transaction txn = new Transaction();

        txn.setTransactionReference(generateTransactionReference());
        txn.setAccount(account);
        txn.setTransactionType("DEPOSIT");
        txn.setAmount(amount);
        txn.setBalanceAfterTransaction(account.getBalance());
        txn.setStatus("SUCCESS");
        txn.setRemarks("Cash Deposit");
        txn.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(txn);
    }

    // ---------------- WITHDRAW ----------------

    @Transactional
    public void withdraw(Long accountId, Double amount) {

        if(amount <= 0)
            throw new RuntimeException("Invalid Amount");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                new RuntimeException("Account Not Found"));

        if(account.getBalance() < amount)
            throw new RuntimeException("Insufficient Balance");

        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);

        Transaction txn = new Transaction();

        txn.setTransactionReference(generateTransactionReference());
        txn.setAccount(account);
        txn.setTransactionType("WITHDRAW");
        txn.setAmount(amount);
        txn.setBalanceAfterTransaction(account.getBalance());
        txn.setStatus("SUCCESS");
        txn.setRemarks("Cash Withdrawal");
        txn.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(txn);
    }

    // ---------------- TRANSFER ----------------

    @Transactional
    public void transfer(Long fromAccountId,
                         Long toAccountId,
                         Double amount) {

        if(amount <= 0)
            throw new RuntimeException("Invalid Amount");

        if(fromAccountId.equals(toAccountId))
            throw new RuntimeException("Cannot transfer to same account");

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() ->
                new RuntimeException("Sender Account Not Found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() ->
                new RuntimeException("Receiver Account Not Found"));

        if(fromAccount.getBalance() < amount)
            throw new RuntimeException("Insufficient Balance");

        // Debit
        fromAccount.setBalance(fromAccount.getBalance() - amount);

        // Credit
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Debit Entry

        Transaction debit = new Transaction();

        debit.setTransactionReference(generateTransactionReference());
        debit.setAccount(fromAccount);
        debit.setTransactionType("TRANSFER_DEBIT");
        debit.setAmount(amount);
        debit.setBalanceAfterTransaction(fromAccount.getBalance());
        debit.setStatus("SUCCESS");
        debit.setRemarks("Transfer To Account : "
                + toAccount.getAccountNumber());
        debit.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(debit);

        // Credit Entry

        Transaction credit = new Transaction();

        credit.setTransactionReference(generateTransactionReference());
        credit.setAccount(toAccount);
        credit.setTransactionType("TRANSFER_CREDIT");
        credit.setAmount(amount);
        credit.setBalanceAfterTransaction(toAccount.getBalance());
        credit.setStatus("SUCCESS");
        credit.setRemarks("Received From Account : "
                + fromAccount.getAccountNumber());
        credit.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(credit);
    }

    // ---------------- MINI STATEMENT ----------------

    public List<Transaction> getTransactions(Long accountId) {

        return transactionRepository
                .findTop10ByAccountAccountIdOrderByTransactionDateDesc(accountId);
    }

    // ---------------- TXN REF ----------------

    private String generateTransactionReference() {

        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .substring(0,8)
                .toUpperCase();
    }

}