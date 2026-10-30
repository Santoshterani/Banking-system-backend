package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.TransactionRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.entity.Account;
import com.example.demo.entity.ApprovalRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

/**
 * NOTE on @Auditable placement: Spring AOP proxies only intercept calls that arrive from
 * OUTSIDE the bean. deposit()/withdraw()/transfer() call executeDeposit()/executeWithdraw()/
 * executeTransfer() on "this" (self-invocation), which bypasses the proxy entirely - so
 * @Auditable is on BOTH the public entry points (fires on the direct-execution path) AND the
 * execute* methods (fires when ApprovalService, a different bean, calls them after a manager
 * approves a parked large transaction). The two paths are mutually exclusive per request, so
 * this never double-logs a single transaction.
 */
@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private static final java.util.Set<String> APPROVAL_EXEMPT_ROLES = java.util.Set.of(
            "BRANCH_MANAGER", "REGIONAL_MANAGER", "ADMIN", "SUPER_ADMIN"
    );

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    @Lazy
    private ApprovalService approvalService;

    @Value("${banking.approval.large-transaction-threshold:50000}")
    private double largeTransactionThreshold;

    @Auditable("DEPOSIT")
    @Transactional
    public TransactionResult deposit(Long accountId, Double amount) {
        validateAmount(amount, "Deposit");
        Account account = getAccountOrThrow(accountId);
        securityUtils.assertBranchAccess(account.getBranch().getBranchId());

        if (needsApproval(amount)) {
            ApprovalRequest req = approvalService.createApproval("DEPOSIT",
                    Map.of("accountId", String.valueOf(accountId)), amount, account.getBranch(), securityUtils.getCurrentUser());
            return TransactionResult.pendingApproval(req.getApprovalId());
        }

        return TransactionResult.completed(executeDeposit(accountId, amount));
    }

    @Auditable("WITHDRAW")
    @Transactional
    public TransactionResult withdraw(Long accountId, Double amount) {
        validateAmount(amount, "Withdrawal");
        Account account = getAccountOrThrow(accountId);
        securityUtils.assertBranchAccess(account.getBranch().getBranchId());

        if (needsApproval(amount)) {
            ApprovalRequest req = approvalService.createApproval("WITHDRAWAL",
                    Map.of("accountId", String.valueOf(accountId)), amount, account.getBranch(), securityUtils.getCurrentUser());
            return TransactionResult.pendingApproval(req.getApprovalId());
        }

        return TransactionResult.completed(executeWithdraw(accountId, amount));
    }

    @Auditable("TRANSFER")
    @Transactional
    public TransactionResult transfer(Long fromAccountId, Long toAccountId, Double amount) {
        validateAmount(amount, "Transfer");
        if (fromAccountId.equals(toAccountId))
            throw new BadRequestException("Cannot transfer to the same account");

        Account fromAccount = getAccountOrThrow(fromAccountId);
        securityUtils.assertBranchAccess(fromAccount.getBranch().getBranchId());

        if (needsApproval(amount)) {
            ApprovalRequest req = approvalService.createApproval("TRANSFER",
                    Map.of("fromAccountId", String.valueOf(fromAccountId), "toAccountId", String.valueOf(toAccountId)),
                    amount, fromAccount.getBranch(), securityUtils.getCurrentUser());
            return TransactionResult.pendingApproval(req.getApprovalId());
        }

        executeTransfer(fromAccountId, toAccountId, amount);
        return TransactionResult.completed(null);
    }

    @Auditable("DEPOSIT")
    @Transactional
    public Transaction executeDeposit(Long accountId, Double amount) {
        Account account = getAccountOrThrow(accountId);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction txn = buildTransaction(account, "DEPOSIT", amount, "Cash Deposit");
        Transaction saved = transactionRepository.save(txn);
        log.info("Deposit {} on account {} - new balance {}", amount, accountId, account.getBalance());
        return saved;
    }

    @Auditable("WITHDRAW")
    @Transactional
    public Transaction executeWithdraw(Long accountId, Double amount) {
        Account account = getAccountOrThrow(accountId);

        if (account.getBalance() < amount)
            throw new InsufficientBalanceException("Insufficient balance in account " + account.getAccountNumber());

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction txn = buildTransaction(account, "WITHDRAW", amount, "Cash Withdrawal");
        Transaction saved = transactionRepository.save(txn);
        log.info("Withdraw {} on account {} - new balance {}", amount, accountId, account.getBalance());
        return saved;
    }

    @Auditable("TRANSFER")
    @Transactional
    public void executeTransfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account fromAccount = getAccountOrThrow(fromAccountId);
        Account toAccount = getAccountOrThrow(toAccountId);

        if (fromAccount.getBalance() < amount)
            throw new InsufficientBalanceException("Insufficient balance in account " + fromAccount.getAccountNumber());

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transactionRepository.save(buildTransaction(fromAccount, "TRANSFER_DEBIT", amount,
                "Transfer To Account : " + toAccount.getAccountNumber()));

        transactionRepository.save(buildTransaction(toAccount, "TRANSFER_CREDIT", amount,
                "Received From Account : " + fromAccount.getAccountNumber()));

        log.info("Transfer {} from {} to {}", amount, fromAccountId, toAccountId);
    }

    public List<Transaction> getTransactions(Long accountId) {
        Account account = getAccountOrThrow(accountId);
        securityUtils.assertBranchAccess(account.getBranch().getBranchId());
        return transactionRepository.findTop10ByAccountAccountIdOrderByTransactionDateDesc(accountId);
    }

    private boolean needsApproval(Double amount) {
        User current = securityUtils.getCurrentUser();
        if (APPROVAL_EXEMPT_ROLES.contains(current.getRole().getRoleName())) {
            return false;
        }
        return amount > largeTransactionThreshold;
    }

    private void validateAmount(Double amount, String label) {
        if (amount == null || amount <= 0)
            throw new BadRequestException(label + " amount must be greater than zero");
    }

    private Account getAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    private Transaction buildTransaction(Account account, String type, Double amount, String remarks) {
        Transaction txn = new Transaction();
        txn.setTransactionReference(generateTransactionReference());
        txn.setAccount(account);
        txn.setTransactionType(type);
        txn.setAmount(amount);
        txn.setBalanceAfterTransaction(account.getBalance());
        txn.setStatus("SUCCESS");
        txn.setRemarks(remarks);
        txn.setTransactionDate(LocalDateTime.now());
        return txn;
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static class TransactionResult {
        private final Transaction transaction;
        private final Long pendingApprovalId;

        private TransactionResult(Transaction transaction, Long pendingApprovalId) {
            this.transaction = transaction;
            this.pendingApprovalId = pendingApprovalId;
        }

        public static TransactionResult completed(Transaction transaction) {
            return new TransactionResult(transaction, null);
        }

        public static TransactionResult pendingApproval(Long approvalId) {
            return new TransactionResult(null, approvalId);
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public Long getPendingApprovalId() {
            return pendingApprovalId;
        }

        public boolean isPendingApproval() {
            return pendingApprovalId != null;
        }
    }
}
