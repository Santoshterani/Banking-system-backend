package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Services.KYCService;
import com.example.demo.dto.request.KYCRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Customer;
import com.example.demo.entity.KYC;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/kyc")
public class KYCController {

    @Autowired
    private KYCService kycService;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasAnyRole('KYC_OFFICER','CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/submit")
    public ApiResponse<KYC> submit(@Valid @RequestBody KYCRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        KYC kyc = new KYC();
        kyc.setCustomer(customer);
        kyc.setAadhaarNumber(request.getAadhaarNumber());
        kyc.setPanNumber(request.getPanNumber());

        return ApiResponse.success("KYC submitted successfully", kycService.submitKYC(kyc));
    }

    @GetMapping("/all")
    public ApiResponse<List<KYC>> getAll() {
        return ApiResponse.success(kycService.getAllKYC());
    }

    @GetMapping("/customer/{id}")
    public ApiResponse<KYC> getCustomer(@PathVariable Long id) {
        return ApiResponse.success(kycService.getCustomerKYC(id));
    }

    @PreAuthorize("hasAnyRole('KYC_OFFICER','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/approve/{id}")
    public ApiResponse<KYC> approve(@PathVariable Long id) {
        return ApiResponse.success("KYC approved successfully", kycService.approve(id));
    }

    @PreAuthorize("hasAnyRole('KYC_OFFICER','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/reject/{id}")
    public ApiResponse<KYC> reject(@PathVariable Long id) {
        return ApiResponse.success("KYC rejected successfully", kycService.reject(id));
    }
}
