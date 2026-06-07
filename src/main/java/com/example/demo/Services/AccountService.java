package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.entity.Account;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    public Account createAccount(Account account) {

        Long customerId =
                account.getCustomer().getCustomerId();

        Long branchId =
                account.getBranch().getBranchId();

        Customer customer =
                customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer Not Found"));

        Branch branch =
                branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new RuntimeException("Branch Not Found"));

        account.setCustomer(customer);
        account.setBranch(branch);

        account.setAccountNumber(
                "ACC" + System.currentTimeMillis());

        account.setBalance(1100.0);

        account.setStatus("ACTIVE");

        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account Not Found"));
    }

    public Account updateAccount(Long id,
                                 Account updatedAccount) {

        Account account =
                accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account Not Found"));

        account.setAccountType(
                updatedAccount.getAccountType());

        account.setStatus(
                updatedAccount.getStatus());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {

        Account account =
                accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account Not Found"));

        accountRepository.delete(account);
    }
}