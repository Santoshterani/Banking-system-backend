package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.LoanRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Loan;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Auditable("APPLY_LOAN")
    public Loan applyLoan(Loan loan) {

        Customer customer = customerRepository
                .findById(loan.getCustomer().getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", loan.getCustomer().getCustomerId()));

        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());

        loan.setCustomer(customer);
        loan.setAppliedDate(LocalDateTime.now());
        loan.setStatus("PENDING");
        loan.setEmiAmount(calculateEMI(loan.getLoanAmount(), loan.getInterestRate(), loan.getTenureMonths()));

        Loan saved = loanRepository.save(loan);
        log.info("Loan applied: id={}, customer={}, amount={}, emi={}",
                saved.getLoanId(), customer.getCustomerId(), saved.getLoanAmount(), saved.getEmiAmount());
        return saved;
    }

    public List<Loan> getCustomerLoans(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());
        return loanRepository.findByCustomerCustomerId(customerId);
    }

    public List<Loan> getAllLoans() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return loanRepository.findAll();
        }
        return loanRepository.findByCustomer_Branch_BranchId(branchFilter);
    }

    public Page<Loan> getAllLoans(Pageable pageable) {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return loanRepository.findAll(pageable);
        }
        return loanRepository.findByCustomer_Branch_BranchId(branchFilter, pageable);
    }

    @Auditable("APPROVE_LOAN")
    public Loan approveLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        securityUtils.assertBranchAccess(loan.getCustomer().getBranch().getBranchId());

        if (!"PENDING".equals(loan.getStatus())) {
            throw new BadRequestException("Only pending loans can be approved");
        }

        loan.setStatus("APPROVED");
        Loan saved = loanRepository.save(loan);
        log.info("Loan approved: id={}", loanId);
        return saved;
    }

    @Auditable("REJECT_LOAN")
    public Loan rejectLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        securityUtils.assertBranchAccess(loan.getCustomer().getBranch().getBranchId());

        if (!"PENDING".equals(loan.getStatus())) {
            throw new BadRequestException("Only pending loans can be rejected");
        }

        loan.setStatus("REJECTED");
        Loan saved = loanRepository.save(loan);
        log.info("Loan rejected: id={}", loanId);
        return saved;
    }

    private Double calculateEMI(Double principal, Double annualRate, Integer months) {
        double monthlyRate = annualRate / (12 * 100);
        return (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);
    }
}
