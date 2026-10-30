package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Services.RDService;
import com.example.demo.dto.request.RDRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Customer;
import com.example.demo.entity.RecurringDeposit;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/rd")
public class RDController {

    @Autowired
    private RDService rdService;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse<RecurringDeposit> create(@Valid @RequestBody RDRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        RecurringDeposit rd = new RecurringDeposit();
        rd.setCustomer(customer);
        rd.setMonthlyDeposit(request.getMonthlyDeposit());
        rd.setInterestRate(request.getInterestRate());
        rd.setTenureMonths(request.getTenureMonths());

        return ApiResponse.success("Recurring Deposit created successfully", rdService.createRD(rd, request.getAccountId()));
    }

    @GetMapping("/all")
    public ApiResponse<List<RecurringDeposit>> getAll() {
        return ApiResponse.success(rdService.getAll());
    }

    @GetMapping("/customer/{id}")
    public ApiResponse<List<RecurringDeposit>> getCustomer(@PathVariable Long id) {
        return ApiResponse.success(rdService.getCustomerRD(id));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/close/{id}")
    public ApiResponse<RecurringDeposit> close(@PathVariable Long id) {
        return ApiResponse.success("Recurring Deposit closed and maturity amount credited", rdService.close(id));
    }
}
