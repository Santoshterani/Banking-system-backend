package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Services.FDService;
import com.example.demo.dto.request.FDRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Customer;
import com.example.demo.entity.FixedDeposit;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/fd")
public class FDController {

    @Autowired
    private FDService fdService;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse<FixedDeposit> create(@Valid @RequestBody FDRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        FixedDeposit fd = new FixedDeposit();
        fd.setCustomer(customer);
        fd.setPrincipalAmount(request.getPrincipalAmount());
        fd.setInterestRate(request.getInterestRate());
        fd.setTenureMonths(request.getTenureMonths());

        return ApiResponse.success("Fixed Deposit created successfully", fdService.createFD(fd, request.getAccountId()));
    }

    @GetMapping("/all")
    public ApiResponse<List<FixedDeposit>> getAll() {
        return ApiResponse.success(fdService.getAllFD());
    }

    @GetMapping("/customer/{id}")
    public ApiResponse<List<FixedDeposit>> getCustomerFD(@PathVariable Long id) {
        return ApiResponse.success(fdService.getCustomerFD(id));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/close/{id}")
    public ApiResponse<FixedDeposit> close(@PathVariable Long id) {
        return ApiResponse.success("Fixed Deposit closed and maturity amount credited", fdService.closeFD(id));
    }
}
