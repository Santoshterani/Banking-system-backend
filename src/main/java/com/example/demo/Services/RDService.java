package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.RDRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.calculator.InterestCalculatorService;
import com.example.demo.dto.response.InterestCalculationResult;
import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.RecurringDeposit;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class RDService {

    private static final Logger log = LoggerFactory.getLogger(RDService.class);

    @Autowired
    private RDRepository rdRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InterestCalculatorService interestCalculatorService;

    @Autowired
    private SecurityUtils securityUtils;

    public RecurringDeposit createRD(RecurringDeposit rd, Long beneficiaryAccountId) {

        if (rd.getCustomer() == null || rd.getCustomer().getCustomerId() == null) {
            throw new BadRequestException("Customer is required to open a Recurring Deposit");
        }
        if (rd.getMonthlyDeposit() == null || rd.getMonthlyDeposit() <= 0) {
            throw new BadRequestException("Monthly deposit must be greater than zero");
        }
        if (rd.getInterestRate() == null || rd.getInterestRate() <= 0) {
            throw new BadRequestException("Interest rate must be greater than zero");
        }
        if (rd.getTenureMonths() == null || rd.getTenureMonths() <= 0) {
            throw new BadRequestException("Tenure must be greater than zero months");
        }

        Customer customer =
                customerRepository.findById(rd.getCustomer().getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", rd.getCustomer().getCustomerId()));

        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());

        Account beneficiaryAccount = resolveBeneficiaryAccount(beneficiaryAccountId, customer.getCustomerId());

        rd.setCustomer(customer);
        rd.setBeneficiaryAccount(beneficiaryAccount);
        rd.setCreatedDate(LocalDateTime.now());
        rd.setStatus("ACTIVE");

        InterestCalculationResult result = interestCalculatorService.calculateRecurringDepositMaturity(
                rd.getMonthlyDeposit(), rd.getInterestRate(), rd.getTenureMonths());
        rd.setMaturityAmount(result.getMaturityAmount());

        RecurringDeposit saved = rdRepository.save(rd);
        log.info("RD {} created for customer {} - monthlyDeposit={}, maturity={}, beneficiaryAccount={}",
                saved.getRdId(), customer.getCustomerId(), saved.getMonthlyDeposit(), saved.getMaturityAmount(),
                beneficiaryAccount.getAccountNumber());
        return saved;
    }

    public List<RecurringDeposit> getAll() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return rdRepository.findAll();
        }
        return rdRepository.findByCustomer_Branch_BranchId(branchFilter);
    }

    public List<RecurringDeposit> getCustomerRD(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());
        return rdRepository.findByCustomerCustomerId(customerId);
    }

    @Auditable("CLOSE_RD")
    public RecurringDeposit close(Long id) {

        RecurringDeposit rd =
                rdRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Recurring Deposit", id));

        securityUtils.assertBranchAccess(rd.getCustomer().getBranch().getBranchId());

        if (!"ACTIVE".equals(rd.getStatus())) {
            throw new BadRequestException("Only an active Recurring Deposit can be closed");
        }

        creditBeneficiary(rd);
        rd.setStatus("CLOSED");

        RecurringDeposit saved = rdRepository.save(rd);
        log.info("RD {} closed and {} credited to account {}", id, rd.getMaturityAmount(), rd.getBeneficiaryAccount().getAccountNumber());
        return saved;
    }

    @Auditable("RD_AUTO_MATURITY_CREDIT")
    public void creditOnMaturity(RecurringDeposit rd) {
        creditBeneficiary(rd);
        rd.setStatus("MATURED");
        rdRepository.save(rd);
    }

    private void creditBeneficiary(RecurringDeposit rd) {
        if (rd.getBeneficiaryAccount() == null) {
            throw new BadRequestException("This Recurring Deposit has no beneficiary account on record. Contact an administrator.");
        }
        transactionService.executeDeposit(rd.getBeneficiaryAccount().getAccountId(), rd.getMaturityAmount());
    }

    private Account resolveBeneficiaryAccount(Long accountId, Long customerId) {
        if (accountId == null) {
            throw new BadRequestException("A beneficiary account is required to receive the maturity payout");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new BadRequestException("The beneficiary account must belong to the same customer opening the Recurring Deposit");
        }
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BadRequestException("The beneficiary account must be ACTIVE");
        }
        return account;
    }
}
