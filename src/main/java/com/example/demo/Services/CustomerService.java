package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    public Customer createCustomer(Customer customer) {

        if(customerRepository.findByEmail(
                customer.getEmail()).isPresent()) {

            throw new RuntimeException(
                    "Email already exists");
        }

        if(customerRepository.findByPhone(
                customer.getPhone()).isPresent()) {

            throw new RuntimeException(
                    "Phone already exists");
        }

        if(customerRepository.findByAadhaarNumber(
                customer.getAadhaarNumber()).isPresent()) {

            throw new RuntimeException(
                    "Aadhaar already exists");
        }

        if(customerRepository.findByPanNumber(
                customer.getPanNumber()).isPresent()) {

            throw new RuntimeException(
                    "PAN already exists");
        }

        Long branchId =
                customer.getBranch().getBranchId();

        Branch branch =
                branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Branch not found"));

        customer.setBranch(branch);
        customer.setCreatedAt(LocalDateTime.now());

        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomer(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));
    }

    public Customer updateCustomer(
            Long id,
            Customer updatedCustomer) {

        Customer customer =
                customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));

        customer.setFirstName(
                updatedCustomer.getFirstName());

        customer.setLastName(
                updatedCustomer.getLastName());

        customer.setEmail(
                updatedCustomer.getEmail());

        customer.setPhone(
                updatedCustomer.getPhone());

        customer.setAddress(
                updatedCustomer.getAddress());

        customer.setAadhaarNumber(
                updatedCustomer.getAadhaarNumber());

        customer.setPanNumber(
                updatedCustomer.getPanNumber());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {

        Customer customer =
                customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));

        customerRepository.delete(customer);
    }
}
