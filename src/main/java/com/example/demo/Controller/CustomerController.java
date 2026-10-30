package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Services.CustomerService;
import com.example.demo.dto.request.CustomerOnboardingRequest;
import com.example.demo.dto.request.CustomerRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Account;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BranchRepository branchRepository;

    /**
     * Primary onboarding path: one form creates the customer AND their first account
     * together, atomically. The frontend's "New Customer" flow calls this exclusively -
     * see CustomerService.onboardCustomer for why (a bank cannot have an account-less
     * customer).
     */
    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/onboard")
    public ApiResponse<Account> onboard(@Valid @RequestBody CustomerOnboardingRequest request) {
        return ApiResponse.success("Customer onboarded with a new " + request.getAccountType() + " account",
                customerService.onboardCustomer(request));
    }

    /**
     * Lower-level endpoint, kept for adding a customer record without an account in one
     * step (e.g. scripted/bulk imports where accounts are created in a separate pass) -
     * not used by the frontend's normal onboarding flow.
     */
    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse<Customer> create(@Valid @RequestBody CustomerRequest request) {

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchId()));

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setAadhaarNumber(request.getAadhaarNumber());
        customer.setPanNumber(request.getPanNumber());
        customer.setBranch(branch);

        return ApiResponse.success("Customer created successfully", customerService.createCustomer(customer));
    }

    @GetMapping("/all")
    public ApiResponse<List<Customer>> all() {
        return ApiResponse.success(customerService.getAllCustomers());
    }

    @GetMapping("/page")
    public ApiResponse<Page<Customer>> page(Pageable pageable) {
        return ApiResponse.success(customerService.getAllCustomers(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> get(@PathVariable Long id) {
        return ApiResponse.success(customerService.getCustomer(id));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        Customer update = new Customer();
        update.setFirstName(request.getFirstName());
        update.setLastName(request.getLastName());
        update.setEmail(request.getEmail());
        update.setPhone(request.getPhone());
        update.setAddress(request.getAddress());
        update.setAadhaarNumber(request.getAadhaarNumber());
        update.setPanNumber(request.getPanNumber());
        return ApiResponse.success("Customer updated successfully", customerService.updateCustomer(id, update));
    }

    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success("Customer deleted successfully", null);
    }
}
