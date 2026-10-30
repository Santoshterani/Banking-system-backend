package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.entity.Account;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public Account createAccount(Account account) {

        Long customerId = account.getCustomer().getCustomerId();
        Long branchId = account.getBranch().getBranchId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        account.setCustomer(customer);
        account.setBranch(branch);
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        account.setBalance(0.0);
        account.setStatus("ACTIVE");
        account.setCreatedAt(LocalDateTime.now());

        Account saved = accountRepository.save(account);
        log.info("Account created: number={}, customer={}, branch={}", saved.getAccountNumber(), customerId, branchId);
        return saved;
    }

    public List<Account> getAllAccounts() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return accountRepository.findAll();
        }
        return accountRepository.findByBranch_BranchId(branchFilter);
    }

    public Page<Account> getAllAccounts(Pageable pageable) {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return accountRepository.findAll(pageable);
        }
        return accountRepository.findByBranch_BranchId(branchFilter, pageable);
    }

    public Account getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
        securityUtils.assertBranchAccess(account.getBranch().getBranchId());
        return account;
    }

    @Auditable("UPDATE_ACCOUNT")
    public Account updateAccount(Long id, Account updatedAccount) {

        Account account = getAccount(id);
        account.setAccountType(updatedAccount.getAccountType());
        account.setStatus(updatedAccount.getStatus());

        Account saved = accountRepository.save(account);
        log.info("Account updated: id={}, status={}", id, saved.getStatus());
        return saved;
    }

    @Auditable("DELETE_ACCOUNT")
    public void deleteAccount(Long id) {
        Account account = getAccount(id);
        accountRepository.delete(account);
        log.info("Account deleted: id={}", id);
    }
}
