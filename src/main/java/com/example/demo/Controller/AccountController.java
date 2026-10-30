package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Services.AccountService;
import com.example.demo.dto.request.AccountRequest;
import com.example.demo.dto.request.AccountUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Account;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse<Account> create(@Valid @RequestBody AccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchId()));

        Account account = new Account();
        account.setCustomer(customer);
        account.setBranch(branch);
        account.setAccountType(request.getAccountType());

        return ApiResponse.success("Account created successfully", accountService.createAccount(account));
    }

    @GetMapping("/all")
    public ApiResponse<List<Account>> all() {
        return ApiResponse.success(accountService.getAllAccounts());
    }

    @GetMapping("/page")
    public ApiResponse<Page<Account>> page(Pageable pageable) {
        return ApiResponse.success(accountService.getAllAccounts(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<Account> get(@PathVariable Long id) {
        return ApiResponse.success(accountService.getAccount(id));
    }

    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public ApiResponse<Account> update(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request) {
        Account update = new Account();
        update.setAccountType(request.getAccountType());
        update.setStatus(request.getStatus());
        return ApiResponse.success("Account updated successfully", accountService.updateAccount(id, update));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ApiResponse.success("Account deleted successfully", null);
    }
}
