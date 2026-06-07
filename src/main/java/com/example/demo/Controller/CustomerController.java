package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.CustomerService;
import com.example.demo.entity.Customer;

@RestController
@RequestMapping("/admin/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public Customer create(
            @RequestBody Customer customer) {

        return customerService
                .createCustomer(customer);
    }

    @GetMapping("/all")
    public List<Customer> all() {

        return customerService
                .getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer get(
            @PathVariable Long id) {

        return customerService
                .getCustomer(id);
    }

    @PutMapping("/update/{id}")
    public Customer update(
            @PathVariable Long id,
            @RequestBody Customer customer) {

        return customerService
                .updateCustomer(id, customer);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return "Customer Deleted";
    }
}
