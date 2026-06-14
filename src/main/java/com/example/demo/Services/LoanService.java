package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.LoanRepository;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Loan;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Apply Loan

    public void applyLoan(Loan loan){

        Customer customer = customerRepository
                .findById(loan.getCustomer().getCustomerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer Not Found"));

        loan.setCustomer(customer);

        loan.setAppliedDate(LocalDateTime.now());

        loan.setStatus("PENDING");

        loan.setEmiAmount(
                calculateEMI(
                        loan.getLoanAmount(),
                        loan.getInterestRate(),
                        loan.getTenureMonths()
                )
        );

        loanRepository.save(loan);

    }

    // Get Customer Loans

    public List<Loan> getCustomerLoans(Long customerId){

        return loanRepository.findByCustomerCustomerId(customerId);

    }

    // Get All Loans

    public List<Loan> getAllLoans(){

        return loanRepository.findAll();

    }

    // Approve Loan

    public void approveLoan(Long loanId){

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan Not Found"));

        loan.setStatus("APPROVED");

        loanRepository.save(loan);

    }

    // Reject Loan

    public void rejectLoan(Long loanId){

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan Not Found"));

        loan.setStatus("REJECTED");

        loanRepository.save(loan);

    }

    // EMI Calculation

    private Double calculateEMI(
            Double principal,
            Double annualRate,
            Integer months){

        double monthlyRate = annualRate / (12 * 100);

        return (principal * monthlyRate *
                Math.pow(1 + monthlyRate, months))
                /
                (Math.pow(1 + monthlyRate, months) - 1);

    }

}
