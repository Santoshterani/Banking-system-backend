package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Services.LoanService;
import com.example.demo.dto.request.LoanRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Loan;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasAnyRole('LOAN_OFFICER','CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/apply")
    public ApiResponse<Loan> applyLoan(@Valid @RequestBody LoanRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanType(request.getLoanType());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setTenureMonths(request.getTenureMonths());

        return ApiResponse.success("Loan applied successfully", loanService.applyLoan(loan));
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Loan>> getCustomerLoans(@PathVariable Long customerId) {
        return ApiResponse.success(loanService.getCustomerLoans(customerId));
    }

    @GetMapping("/all")
    public ApiResponse<List<Loan>> getAllLoans() {
        return ApiResponse.success(loanService.getAllLoans());
    }

    @GetMapping("/page")
    public ApiResponse<Page<Loan>> page(Pageable pageable) {
        return ApiResponse.success(loanService.getAllLoans(pageable));
    }

    @PreAuthorize("hasAnyRole('LOAN_OFFICER','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/approve/{loanId}")
    public ApiResponse<Loan> approveLoan(@PathVariable Long loanId) {
        return ApiResponse.success("Loan approved successfully", loanService.approveLoan(loanId));
    }

    @PreAuthorize("hasAnyRole('LOAN_OFFICER','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/reject/{loanId}")
    public ApiResponse<Loan> rejectLoan(@PathVariable Long loanId) {
        return ApiResponse.success("Loan rejected successfully", loanService.rejectLoan(loanId));
    }
}
