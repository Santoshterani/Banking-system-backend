package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.LoanService;
import com.example.demo.entity.Loan;

@RestController
@RequestMapping("/admin/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // Apply Loan
    @PostMapping("/apply")
    public String applyLoan(@RequestBody Loan loan) {

        try {

            loanService.applyLoan(loan);

            return "Loan Applied Successfully";

        } catch (Exception e) {

            return e.getMessage();
        }
    }

    // Get Customer Loans

    @GetMapping("/customer/{customerId}")
    public List<Loan> getCustomerLoans(
            @PathVariable Long customerId){

        return loanService.getCustomerLoans(customerId);

    }

    // Get All Loans

    @GetMapping("/all")
    public List<Loan> getAllLoans(){

        return loanService.getAllLoans();

    }

    // Approve Loan

    @PutMapping("/approve/{loanId}")
    public String approveLoan(
            @PathVariable Long loanId){

        try {

            loanService.approveLoan(loanId);

            return "Loan Approved";

        } catch (Exception e) {

            return e.getMessage();
        }

    }

    // Reject Loan

    @PutMapping("/reject/{loanId}")
    public String rejectLoan(
            @PathVariable Long loanId){

        try {

            loanService.rejectLoan(loanId);

            return "Loan Rejected";

        } catch (Exception e) {

            return e.getMessage();
        }

    }

}
